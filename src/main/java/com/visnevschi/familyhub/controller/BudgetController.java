package com.visnevschi.familyhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.document.Budget;
import com.visnevschi.familyhub.dto.Budget.BudgetCreationDTO;
import com.visnevschi.familyhub.dto.Budget.TransactionCreationDTO;
import com.visnevschi.familyhub.service.BudgetService;
import com.visnevschi.familyhub.service.FamilyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping({"/budget", "/api/budget"})
public class BudgetController {

    private final FamilyService familyService;
    private final BudgetService budgetService;

    public BudgetController(FamilyService familyService, BudgetService budgetService) {
        this.familyService = familyService;
        this.budgetService = budgetService;
    }

    @GetMapping("")
    public Budget getBudget(@AuthenticationPrincipal Jwt jwt) {
        Long familyId = familyService.getFamilyIdForUser(jwt.getSubject());
        return budgetService.getBudgetForFamily(familyId);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Budget createBudget(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody BudgetCreationDTO request) {
        Long familyId = familyService.getFamilyIdForUser(jwt.getSubject());
        if (request.getParentBudgetId() != null) {
            budgetService.createSubBudget(familyId, request.getParentBudgetId(), request.getName(), request.getCurrencyISOCode());
        } else {
            budgetService.createNewBudgetForFamily(familyId, request.getName(), request.getCurrencyISOCode());
        }
        return budgetService.getBudgetForFamily(familyId);
    }

    @DeleteMapping("/{budgetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBudget(@AuthenticationPrincipal Jwt jwt, @PathVariable String budgetId) {
        Long familyId = familyService.getFamilyIdForUser(jwt.getSubject());
        budgetService.deleteBudget(familyId, budgetId);
    }

    @PatchMapping("/{budgetId}")
    public Budget modifyBudget(@AuthenticationPrincipal Jwt jwt, @PathVariable String budgetId, @Valid @RequestBody BudgetCreationDTO request) {
        Long familyId = familyService.getFamilyIdForUser(jwt.getSubject());
        budgetService.modifyBudget(familyId, budgetId, request.getName(), request.getCurrencyISOCode());
        return budgetService.getBudgetForFamily(familyId);
    }

    @PostMapping("/{budgetId}/transaction")
    @ResponseStatus(HttpStatus.CREATED)
    public Budget addTransaction(@AuthenticationPrincipal Jwt jwt, @PathVariable String budgetId, @Valid @RequestBody TransactionCreationDTO request) {
        Long familyId = familyService.getFamilyIdForUser(jwt.getSubject());
        budgetService.addTransaction(familyId, budgetId, request);
        return budgetService.getBudgetForFamily(familyId);
    }

    @DeleteMapping("/{budgetId}/transaction/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@AuthenticationPrincipal Jwt jwt, @PathVariable String budgetId, @PathVariable String transactionId) {
        Long familyId = familyService.getFamilyIdForUser(jwt.getSubject());
        budgetService.deleteTransaction(familyId, budgetId, transactionId);
    }

    @PatchMapping("/{budgetId}/transaction/{transactionId}")
    public Budget modifyTransaction(@AuthenticationPrincipal Jwt jwt, @PathVariable String budgetId, @PathVariable String transactionId, @Valid @RequestBody TransactionCreationDTO request) {
        Long familyId = familyService.getFamilyIdForUser(jwt.getSubject());
        budgetService.modifyTransaction(familyId, budgetId, transactionId, request);
        return budgetService.getBudgetForFamily(familyId);
    }
}