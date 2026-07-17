package com.visnevschi.familyhub.document;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.visnevschi.familyhub.dto.Calendar.RecurrenceFrequency;

public class EventRecurrence {
    private RecurrenceFrequency frequency;
    private Integer interval;
    private Instant until;
    private Integer count;
    private Set<DayOfWeek> byWeekDays = new HashSet<>();
    private Integer byMonthDay;

    public EventRecurrence() {
    }

    public EventRecurrence(RecurrenceFrequency frequency, Integer interval, Instant until, Integer count,
                           Set<DayOfWeek> byWeekDays, Integer byMonthDay) {
        this.frequency = frequency;
        this.interval = interval;
        this.until = until;
        this.count = count;
        this.byWeekDays = byWeekDays == null ? new HashSet<>() : new HashSet<>(byWeekDays);
        this.byMonthDay = byMonthDay;
    }

    public RecurrenceFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(RecurrenceFrequency frequency) {
        this.frequency = frequency;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Instant getUntil() {
        return until;
    }

    public void setUntil(Instant until) {
        this.until = until;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Set<DayOfWeek> getByWeekDays() {
        return byWeekDays;
    }

    public void setByWeekDays(Set<DayOfWeek> byWeekDays) {
        this.byWeekDays = byWeekDays == null ? new HashSet<>() : new HashSet<>(byWeekDays);
    }

    public Integer getByMonthDay() {
        return byMonthDay;
    }

    public void setByMonthDay(Integer byMonthDay) {
        this.byMonthDay = byMonthDay;
    }
}