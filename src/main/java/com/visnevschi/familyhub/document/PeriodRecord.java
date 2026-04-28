package com.visnevschi.familyhub.document;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Field;

public class PeriodRecord {

    @Field("id")
    private String id;

    @Field("start_date")
    private LocalDate startDate;

    @Field("end_date")
    private LocalDate endDate;

    protected PeriodRecord() {
    }

    public PeriodRecord(String id, LocalDate startDate) {
        this.id = id;
        this.startDate = startDate;
    }

    public String getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
