package com.visnevschi.familyhub.dto.Calendar;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RecurrenceRuleRequest {

    @NotNull
    private RecurrenceFrequency frequency;

    @Min(1)
    private Integer interval = 1;

    private Instant until;

    @Min(1)
    private Integer count;

    private final Set<DayOfWeek> byWeekDays = new HashSet<>();

    @Min(1)
    @Max(31)
    private Integer byMonthDay;

    public RecurrenceFrequency getFrequency() {
        return frequency;
    }

    public Integer getInterval() {
        return interval;
    }

    public Instant getUntil() {
        return until;
    }

    public Integer getCount() {
        return count;
    }

    public Set<DayOfWeek> getByWeekDays() {
        return byWeekDays;
    }

    public Integer getByMonthDay() {
        return byMonthDay;
    }
}