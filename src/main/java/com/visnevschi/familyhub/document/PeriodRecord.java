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

    @Field("period_length_days")
    private Integer periodLengthDays;

    protected PeriodRecord() {
    }

    public PeriodRecord(String id, LocalDate startDate, Integer periodLengthDays) {
        this.id = id;
        this.periodLengthDays = periodLengthDays != null ? periodLengthDays : 5;
        this.startDate = java.util.Objects.requireNonNull(startDate, "startDate is required");
        this.endDate = calculateEndDate(this.startDate, this.periodLengthDays);
    }

    public String getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = java.util.Objects.requireNonNull(startDate, "startDate is required");
        this.endDate = calculateEndDate(this.startDate, this.periodLengthDays);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public Integer getPeriodLengthDays() {
        return periodLengthDays;
    }

    public void setPeriodLengthDays(Integer periodLengthDays) {
        this.periodLengthDays = periodLengthDays != null ? periodLengthDays : 5;
        if (this.startDate != null) {
            this.endDate = calculateEndDate(this.startDate, this.periodLengthDays);
        }
    }

    private static LocalDate calculateEndDate(LocalDate startDate, Integer periodLengthDays) {
        int length = periodLengthDays != null ? periodLengthDays : 5;
        return startDate.plusDays(Math.max(1, length) - 1L);
    }
}
