package com.visnevschi.familyhub.document;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class Transaction {
    @Id
    private String id;

    @Field("description")
    private String description;

    @Field("amount")
    private BigDecimal amount;

    @Field("timestamp")
    private long timestamp;

    @Field("currency_iso_code")
    private String currencyISOCode;

    public Transaction() {
    }

    public Transaction(String id, String description, BigDecimal amount, long timestamp, String currencyISOCode) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.timestamp = timestamp;
        this.currencyISOCode = currencyISOCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrencyISOCode() {
        return currencyISOCode;
    }

    public void setCurrencyISOCode(String currencyISOCode) {
        this.currencyISOCode = currencyISOCode;
    }


}
