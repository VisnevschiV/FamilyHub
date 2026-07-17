package com.visnevschi.familyhub.dto.Calendar;

import java.time.DayOfWeek;
import java.time.Instant;
import java.util.Set;

import com.visnevschi.familyhub.document.EventRecurrence;

public record RecurrenceRuleResponse(
        RecurrenceFrequency frequency,
        Integer interval,
        Instant until,
        Integer count,
        Set<DayOfWeek> byWeekDays,
        Integer byMonthDay
) {
    public static RecurrenceRuleResponse fromDocument(EventRecurrence recurrence) {
        if (recurrence == null) {
            return null;
        }

        return new RecurrenceRuleResponse(
                recurrence.getFrequency(),
                recurrence.getInterval(),
                recurrence.getUntil(),
                recurrence.getCount(),
                recurrence.getByWeekDays() == null ? Set.of() : Set.copyOf(recurrence.getByWeekDays()),
                recurrence.getByMonthDay()
        );
    }
}