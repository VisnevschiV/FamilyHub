package com.visnevschi.familyhub.dto.Calendar;

import java.time.Instant;
import java.util.Set;

import com.visnevschi.familyhub.document.CalendarEvent;

public record CalendarEventOccurrenceResponse(
        String eventId,
        Long familyId,
        String title,
        String description,
        Instant occurrenceStart,
        Instant occurrenceEnd,
        boolean allDayEvent,
        Set<Long> participants
) {
    public static CalendarEventOccurrenceResponse fromEventAndOccurrence(CalendarEvent event, Instant occurrenceStart, Instant occurrenceEnd) {
        return new CalendarEventOccurrenceResponse(
                event.getId(),
                event.getFamilyId(),
                event.getTitle(),
                event.getDescription(),
                occurrenceStart,
                occurrenceEnd,
                event.isAllDayEvent(),
                event.getParticipants() == null ? Set.of() : Set.copyOf(event.getParticipants())
        );
    }
}