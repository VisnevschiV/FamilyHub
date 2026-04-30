package com.visnevschi.familyhub.dto.Budget;

public class BudgetCreationDTO {
    private String name;
    private String currencyISOCode;
    private String parentBudgetId;

    public BudgetCreationDTO() {}

    public BudgetCreationDTO(String name, String currencyISOCode, String parentBudgetId) {
        this.name = name;
        this.currencyISOCode = currencyISOCode;
        this.parentBudgetId = parentBudgetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencyISOCode() {
        return currencyISOCode;
    }

    public void setCurrencyISOCode(String currencyISOCode) {
        this.currencyISOCode = currencyISOCode;
    }

    public String getParentBudgetId() {
        return parentBudgetId;
    }

    public void setParentBudgetId(String parentBudgetId) {
        this.parentBudgetId = parentBudgetId;
    }
}
