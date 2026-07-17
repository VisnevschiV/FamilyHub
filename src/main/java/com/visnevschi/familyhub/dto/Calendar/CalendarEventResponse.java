package com.visnevschi.familyhub.dto.Calendar;

import java.time.Instant;
import java.util.Set;

import com.visnevschi.familyhub.document.CalendarEvent;

public record CalendarEventResponse(
        String id,
        Long familyId,
        String title,
        String description,
        Instant time,
        Instant endTime,
        boolean allDayEvent,
        RecurrenceRuleResponse recurrence,
        Set<Long> participants
) {
    public static CalendarEventResponse fromDocument(CalendarEvent event) {
        return new CalendarEventResponse(
                event.getId(),
                event.getFamilyId(),
                event.getTitle(),
                event.getDescription(),
                event.getTime(),
                event.getEndTime(),
                event.isAllDayEvent(),
                RecurrenceRuleResponse.fromDocument(event.getRecurrence()),
                event.getParticipants() == null ? Set.of() : Set.copyOf(event.getParticipants())
        );
    }
}