package com.visnevschi.familyhub.dto.Budget;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TransactionCreationDTO {
    private String budgetId;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "amount is required")
    private BigDecimal amount;

    @NotBlank(message = "currencyIsoCode is required")
    private String currencyIsoCode;

    public TransactionCreationDTO() {}

    public TransactionCreationDTO(String budgetId, String description, BigDecimal amount, String currencyIsoCode) {
        this.budgetId = budgetId;
        this.description = description;
        this.amount = amount;
        this.currencyIsoCode = currencyIsoCode;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyIsoCode() {
        return currencyIsoCode;
    }

    public void setCurrencyIsoCode(String currencyIsoCode) {
        this.currencyIsoCode = currencyIsoCode;
    }
}
