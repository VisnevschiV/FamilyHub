package com.visnevschi.familyhub.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.document.Budget;
import com.visnevschi.familyhub.document.Transaction;
import com.visnevschi.familyhub.dto.Budget.TransactionCreationDTO;
import com.visnevschi.familyhub.exception.NotFoundException;
import com.visnevschi.familyhub.repository.BudgetRepository;


@Service
@SuppressWarnings("null")
public class BudgetService {
    private static final Logger log = LoggerFactory.getLogger(BudgetService.class);
    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public Budget getBudgetForFamily(Long familyId) {
        List<Budget> all = budgetRepository.findAllByFamilyId(familyId);
        if (all.isEmpty()) {
            return createNewBudgetForFamily(familyId);
        }
        if (all.size() == 1) {
            return all.get(0);
        }
        // Duplicates exist (caused by a previous bug). Keep the richest document
        // (most transactions + sub-budgets) and delete the rest.
        log.warn("Found {} budget documents for familyId={}, deduplicating", all.size(), familyId);
        Budget keeper = all.stream().max(java.util.Comparator.comparingInt(b -> {
            int t = b.getTransactions() != null ? b.getTransactions().size() : 0;
            int s = b.getSubBudgets() != null ? b.getSubBudgets().size() : 0;
            return t + s;
        })).get();
        all.stream()
           .filter(b -> !b.getId().equals(keeper.getId()))
           .forEach(budgetRepository::delete);
        return keeper;
    }

    public Budget createNewBudgetForFamily(Long familyId) {
        Budget budget = new Budget(familyId);
        return budgetRepository.save(budget);
    }

    public Budget createNewBudgetForFamily(Long familyId, String name, String currencyISOCode) {
        Budget budget = new Budget(familyId);
        if (name != null) {
            budget.setName(name);
        }
        if (currencyISOCode != null) {
            validateCurrency(currencyISOCode);
            budget.setCurrencyISOCode(currencyISOCode);
        }
        return budgetRepository.save(budget);
    }


    public void addBudget(Budget budget, String fatherBudgetId, Long familyId) {
        Budget rootBudget = requireRootBudget(familyId);
        Budget fatherBudget = DFSBudgetSearch(rootBudget, fatherBudgetId);
        if (fatherBudget == null) {
            throw new NotFoundException("Father budget not found");
        }
        if (fatherBudget.getSubBudgets() == null) {
            fatherBudget.setSubBudgets(new ArrayList<>());
        }
        fatherBudget.getSubBudgets().add(budget);
        budgetRepository.save(rootBudget);
    }

    public void createSubBudget(Long familyId, String parentBudgetId, String name, String currencyISOCode) {
        Budget rootBudget = requireRootBudget(familyId);
        Budget parentBudget = DFSBudgetSearch(rootBudget, parentBudgetId);
        if (parentBudget == null) {
            throw new NotFoundException("Parent budget not found: " + parentBudgetId);
        }

        String resolvedCurrency = currencyISOCode != null ? currencyISOCode
                : (rootBudget.getCurrencyISOCode() != null ? rootBudget.getCurrencyISOCode() : "EUR");
        if (currencyISOCode != null) {
            validateCurrency(currencyISOCode);
        }

        Budget subBudget = new Budget(
                UUID.randomUUID().toString(),
                familyId,
                name != null ? name : "Sub Budget",
                new ArrayList<>(),
                new ArrayList<>(),
                resolvedCurrency
        );

        parentBudget.getSubBudgets().add(subBudget);
        budgetRepository.save(rootBudget);
        log.info("Sub-budget created under parentBudgetId={} for familyId={}", parentBudgetId, familyId);
    }
    public void deleteBudget(Long familyId, String budgetId) {
        Budget rootBudget = requireRootBudget(familyId);
        if (budgetId.equals(rootBudget.getId())) {
            budgetRepository.delete(rootBudget);
            return;
        }
        boolean removed = removeBudgetFromTree(rootBudget, budgetId);
        if (!removed) {
            throw new NotFoundException("Budget not found: " + budgetId);
        }
        budgetRepository.save(rootBudget);
    }

    public void modifyBudget(Long familyId, String budgetId, String newName, String newCurrencyISOCode) {
        Budget rootBudget = requireRootBudget(familyId);
        Budget budget = DFSBudgetSearch(rootBudget, budgetId);
        if (budget == null) {
            throw new NotFoundException("Budget not found");
        }
        if (newName != null) {
            budget.setName(newName);
        }
        if (newCurrencyISOCode != null) {
            validateCurrency(newCurrencyISOCode);
            budget.setCurrencyISOCode(newCurrencyISOCode);
        }
        budgetRepository.save(rootBudget);
    }

    public void addTransaction(Long familyId, String budgetId, TransactionCreationDTO request) {
        if (request.getCurrencyISOCode() == null) {
            throw new IllegalArgumentException("currencyISOCode is required");
        }
        validateCurrency(request.getCurrencyISOCode());
        
        Budget rootBudget = requireRootBudget(familyId);
        Budget budget = DFSBudgetSearch(rootBudget, budgetId);
        if (budget == null) {
            throw new NotFoundException("Budget not found");
        }
        
        BigDecimal normalizedAmount = normalizeBigDecimal(request.getAmount(), request.getCurrencyISOCode());
        Transaction transaction = new Transaction(
            UUID.randomUUID().toString(),
            request.getDescription(),
            normalizedAmount,
            System.currentTimeMillis(),
            request.getCurrencyISOCode()
        );

        if (budget.getTransactions() == null) {
            budget.setTransactions(new ArrayList<>());
        }
        budget.getTransactions().add(transaction);
        budgetRepository.save(rootBudget);
        log.info("Transaction added to budget={} for familyId={}", budgetId, familyId);
    }

    public void deleteTransaction(Long familyId, String budgetId, String transactionId) {
        Budget rootBudget = requireRootBudget(familyId);
        Budget budget = DFSBudgetSearch(rootBudget, budgetId);
        if (budget == null) {
            throw new NotFoundException("Budget not found");
        }
        if (budget.getTransactions() == null) {
            budget.setTransactions(new ArrayList<>());
        }
        budget.getTransactions().removeIf(t -> t.getId().equals(transactionId));
        budgetRepository.save(rootBudget);
    }

    public void modifyTransaction(Long familyId, String budgetId, String transactionId, TransactionCreationDTO request) {
        if (request.getCurrencyISOCode() != null) {
            validateCurrency(request.getCurrencyISOCode());
        }
        
        Budget rootBudget = requireRootBudget(familyId);
        Budget budget = DFSBudgetSearch(rootBudget, budgetId);
        if (budget == null) {
            throw new NotFoundException("Budget not found");
        }
        if (budget.getTransactions() == null) {
            budget.setTransactions(new ArrayList<>());
        }
        
        boolean found = false;
        for (Transaction transaction : budget.getTransactions()) {
            if (transaction.getId().equals(transactionId)) {
                if (request.getDescription() != null) {
                    transaction.setDescription(request.getDescription());
                }
                if (request.getAmount() != null) {
                    BigDecimal normalizedAmount = normalizeBigDecimal(request.getAmount(), 
                        request.getCurrencyISOCode() != null ? request.getCurrencyISOCode() : transaction.getCurrencyISOCode());
                    transaction.setAmount(normalizedAmount);
                }
                if (request.getCurrencyISOCode() != null) {
                    transaction.setCurrencyISOCode(request.getCurrencyISOCode());
                }
                found = true;
                break;
            }
        }
        if (!found) {
            throw new NotFoundException("Transaction not found: " + transactionId);
        }
        budgetRepository.save(rootBudget);
        log.info("Transaction modified: budgetId={}, transactionId={}, familyId={}", budgetId, transactionId, familyId);
    }


    public Budget findBudgetById(String budgetId, Long familyId) {
        Budget root = requireRootBudget(familyId);
        return DFSBudgetSearch(root, budgetId);
    }

    private Budget requireRootBudget(Long familyId) {
        return Objects.requireNonNull(getBudgetForFamily(familyId), "Budget not found");
    }

    public Budget DFSBudgetSearch(Budget currentBudget, String targetId) {
        if (currentBudget == null) {
            return null;
        }

        if (targetId.equals(currentBudget.getId())) {
            return currentBudget;
        }

        List<Budget> subBudgets = currentBudget.getSubBudgets();
        if (subBudgets == null || subBudgets.isEmpty()) {
            return null;
        }

        for (Budget subBudget : subBudgets) {
            Budget result = DFSBudgetSearch(subBudget, targetId);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private boolean removeBudgetFromTree(Budget current, String targetId) {
        List<Budget> subs = current.getSubBudgets();
        if (subs == null) return false;
        if (subs.removeIf(b -> b.getId().equals(targetId))) return true;
        for (Budget sub : subs) {
            if (removeBudgetFromTree(sub, targetId)) return true;
        }
        return false;
    }

    private void validateCurrency(String currencyCode) {
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid currency code: " + currencyCode, e);
        }
    }

    private BigDecimal normalizeBigDecimal(BigDecimal amount, String currencyCode) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        int scale = Currency.getInstance(currencyCode).getDefaultFractionDigits();
        return amount.setScale(scale, RoundingMode.HALF_UP);
    }
}
