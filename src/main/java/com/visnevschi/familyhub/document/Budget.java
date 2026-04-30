package com.visnevschi.familyhub.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "budgets")
public class Budget {
    @Id
    private String id;

    @Field("family_id")
    private Long familyId;

    @Field("name")
    private String name;

    @Field("transactions")
    private List<Transaction> transactions;

    @Field("sub_budgets")
    private List<Budget> subBudgets;

    @Field("currency_iso_code")
    private String currencyISOCode;

    protected Budget() {
        this.transactions = new ArrayList<>();
        this.subBudgets = new ArrayList<>();
    }

    public Budget(Long familyId) {
        this.familyId = familyId;
        this.name = "Family Budget";
        this.currencyISOCode = "EUR";
        this.transactions = new ArrayList<>();
        this.subBudgets = new ArrayList<>();
    }

    public Budget(String id, Long familyId, String name, List<Transaction> transactions, List<Budget> subBudgets, String currencyISOCode) {
        this.id = id;
        this.familyId = familyId;
        this.name = name;
        this.transactions = transactions == null ? new ArrayList<>() : transactions;
        this.subBudgets = subBudgets == null ? new ArrayList<>() : subBudgets;
        this.currencyISOCode = currencyISOCode;
    }

    public List<Budget> getSubBudgets() {
        return subBudgets;
    }

    public void setSubBudgets(List<Budget> subBudgets) {
        this.subBudgets = subBudgets == null ? new ArrayList<>() : subBudgets;
    }

    public void addSubBudget(Budget subBudget) {
        this.subBudgets.add(subBudget);
    }

    public void removeSubBudget(String subBudgetId) {
        this.subBudgets.removeIf(budget -> budget.getId().equals(subBudgetId));
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions == null ? new ArrayList<>() : transactions;
    }

    public String getCurrencyISOCode() {
        return currencyISOCode;
    }

    public void setCurrencyISOCode(String currencyISOCode) {
        this.currencyISOCode = currencyISOCode;
    }
}
