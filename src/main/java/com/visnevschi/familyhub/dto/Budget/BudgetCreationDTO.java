package com.visnevschi.familyhub.dto.Budget;

public class BudgetCreationDTO {
    private String name;
    private String currencyIsoCode;
    private String parentBudgetId;

    public BudgetCreationDTO() {}

    public BudgetCreationDTO(String name, String currencyIsoCode, String parentBudgetId) {
        this.name = name;
        this.currencyIsoCode = currencyIsoCode;
        this.parentBudgetId = parentBudgetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public void setCurrencyIsoCode(String currencyIsoCode) {
        this.currencyIsoCode = currencyIsoCode;
    }

    public String getParentBudgetId() {
        return parentBudgetId;
    }

    public void setParentBudgetId(String parentBudgetId) {
        this.parentBudgetId = parentBudgetId;
    }
}
