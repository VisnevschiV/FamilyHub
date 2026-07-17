package com.visnevschi.familyhub.service;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.document.CalendarEvent;
import com.visnevschi.familyhub.document.EventRecurrence;
import com.visnevschi.familyhub.dto.Calendar.CalendarEventOccurrenceResponse;
import com.visnevschi.familyhub.dto.Calendar.RecurrenceFrequency;
import com.visnevschi.familyhub.dto.Calendar.RecurrenceRuleRequest;
import com.visnevschi.familyhub.exception.NotFoundException;
import com.visnevschi.familyhub.repository.CalendarEventRepository;

@Service
public class CalendarService {
    private final FamilyService familyService;
    private final CalendarEventRepository calendarEventRepository;
    private final NotificationService notificationService;
    private final PersonaService personaService;

    public CalendarService(FamilyService familyService, CalendarEventRepository calendarEventRepository, NotificationService notificationService, PersonaService personaService) {
        this.familyService = familyService;
        this.calendarEventRepository = calendarEventRepository;
        this.notificationService = notificationService;
        this.personaService = personaService;
    }

    public CalendarEvent createEvent(String userEmail, String title, String description, Instant time, Instant endTime, boolean allDayEvent,
                                     RecurrenceRuleRequest recurrence, java.util.Set<Long> participants) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);

        validateEventTiming(time, endTime, allDayEvent, recurrence);

        CalendarEvent event = new CalendarEvent(title, description, time, allDayEvent ? null : endTime, allDayEvent, familyId);
        event.setRecurrence(toEventRecurrence(recurrence));
        if (participants != null && !participants.isEmpty()) {
            event.setParticipants(new java.util.HashSet<>(participants));
        }
        CalendarEvent savedEvent = calendarEventRepository.save(event);
        Long creatorId = personaService.getForEmail(userEmail).getId();
        notificationService.createNotification(creatorId, "New event created: " + title);
        return savedEvent;
    }

    public void deleteEvent(String userEmail, String eventId) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);

        CalendarEvent event = calendarEventRepository.findById(Objects.requireNonNull(eventId))
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getFamilyId().equals(familyId)) {
            throw new IllegalStateException("User does not have permission to delete this event");
        } 

        calendarEventRepository.deleteById(Objects.requireNonNull(eventId));
    }

    public java.util.List<CalendarEvent> getEventsForFamily(String userEmail) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        return calendarEventRepository.findByFamilyIdOrderByTimeAsc(familyId);
    }

    public List<CalendarEventOccurrenceResponse> getEventOccurrencesForFamily(String userEmail, Instant start, Instant end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end are required");
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start must be before end");
        }

        List<CalendarEvent> events = getEventsForFamily(userEmail);
        List<CalendarEventOccurrenceResponse> occurrences = new ArrayList<>();

        for (CalendarEvent event : events) {
            occurrences.addAll(expandEventOccurrences(event, start, end));
        }

        occurrences.sort(Comparator.comparing(item -> item.occurrenceStart()));
        return occurrences;
    }

    public CalendarEvent updateEvent(String userEmail, String eventId, String title, String description, Instant time, Instant endTime,
                                     boolean allDayEvent, RecurrenceRuleRequest recurrence, java.util.Set<Long> participants) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);

        CalendarEvent event = calendarEventRepository.findById(Objects.requireNonNull(eventId))
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getFamilyId().equals(familyId)) {
            throw new IllegalStateException("User does not have permission to update this event");
        }

        validateEventTiming(time, endTime, allDayEvent, recurrence);

        event.setTitle(title);
        event.setDescription(description);
        event.setTime(time);
        event.setEndTime(allDayEvent ? null : endTime);
        event.setAllDayEvent(allDayEvent);
        event.setRecurrence(toEventRecurrence(recurrence));
        event.setParticipants(participants != null ? new java.util.HashSet<>(participants) : new java.util.HashSet<>());
        return calendarEventRepository.save(event);
    }

    private void validateEventTiming(Instant time, Instant endTime, boolean allDayEvent, RecurrenceRuleRequest recurrence) {
        if (time == null) {
            throw new IllegalArgumentException("Event start time is required");
        }

        if (!allDayEvent && endTime != null && endTime.isBefore(time)) {
            throw new IllegalArgumentException("Event end time must be after start time");
        }

        validateRecurrence(recurrence);
    }

    private void validateRecurrence(RecurrenceRuleRequest recurrence) {
        if (recurrence == null) {
            return;
        }

        if (recurrence.getFrequency() == null) {
            throw new IllegalArgumentException("Recurrence frequency is required");
        }

        Integer intervalValue = recurrence.getInterval();
        int interval = intervalValue == null ? 1 : intervalValue.intValue();
        if (interval <= 0) {
            throw new IllegalArgumentException("Recurrence interval must be greater than zero");
        }

        if (recurrence.getUntil() != null && recurrence.getCount() != null) {
            throw new IllegalArgumentException("Recurrence can have either until or count, not both");
        }

        if (recurrence.getFrequency() == RecurrenceFrequency.WEEKLY && recurrence.getByWeekDays().isEmpty()) {
            throw new IllegalArgumentException("Weekly recurrence requires at least one weekday");
        }

        if (recurrence.getFrequency() != RecurrenceFrequency.WEEKLY && !recurrence.getByWeekDays().isEmpty()) {
            throw new IllegalArgumentException("Weekdays can only be specified for weekly recurrence");
        }

        if (recurrence.getFrequency() == RecurrenceFrequency.MONTHLY && recurrence.getByMonthDay() == null) {
            throw new IllegalArgumentException("Monthly recurrence requires a month day");
        }

        if (recurrence.getFrequency() != RecurrenceFrequency.MONTHLY && recurrence.getByMonthDay() != null) {
            throw new IllegalArgumentException("Month day can only be specified for monthly recurrence");
        }
    }

    private EventRecurrence toEventRecurrence(RecurrenceRuleRequest recurrence) {
        if (recurrence == null) {
            return null;
        }

        Integer requestedInterval = recurrence.getInterval();
        Integer interval = requestedInterval == null ? Integer.valueOf(1) : requestedInterval;
        return new EventRecurrence(
                recurrence.getFrequency(),
                interval,
                recurrence.getUntil(),
                recurrence.getCount(),
                recurrence.getByWeekDays(),
                recurrence.getByMonthDay()
        );
    }

    private List<CalendarEventOccurrenceResponse> expandEventOccurrences(CalendarEvent event, Instant rangeStart, Instant rangeEnd) {
        EventRecurrence recurrence = event.getRecurrence();
        if (recurrence == null) {
            Instant occurrenceEnd = event.getEndTime();
            if (intersects(rangeStart, rangeEnd, event.getTime(), occurrenceEnd)) {
                return List.of(CalendarEventOccurrenceResponse.fromEventAndOccurrence(event, event.getTime(), occurrenceEnd));
            }
            return List.of();
        }

        return switch (recurrence.getFrequency()) {
            case DAILY -> expandDaily(event, recurrence, rangeStart, rangeEnd);
            case WEEKLY -> expandWeekly(event, recurrence, rangeStart, rangeEnd);
            case MONTHLY -> expandMonthly(event, recurrence, rangeStart, rangeEnd);
            case YEARLY -> expandYearly(event, recurrence, rangeStart, rangeEnd);
        };
    }

    private List<CalendarEventOccurrenceResponse> expandDaily(CalendarEvent event, EventRecurrence recurrence, Instant rangeStart, Instant rangeEnd) {
        List<CalendarEventOccurrenceResponse> output = new ArrayList<>();
        ZonedDateTime cursor = event.getTime().atZone(ZoneOffset.UTC);
        int interval = safeInterval(recurrence);
        int generated = 0;
        long durationSeconds = occurrenceDurationSeconds(event);

        while (true) {
            Instant occurrenceStart = cursor.toInstant();
            if (!isWithinRecurrenceLimit(occurrenceStart, recurrence, generated)) {
                break;
            }

            generated++;
            Instant occurrenceEnd = durationSeconds >= 0 ? occurrenceStart.plusSeconds(durationSeconds) : null;
            if (intersects(rangeStart, rangeEnd, occurrenceStart, occurrenceEnd)) {
                output.add(CalendarEventOccurrenceResponse.fromEventAndOccurrence(event, occurrenceStart, occurrenceEnd));
            }

            if (!occurrenceStart.isBefore(rangeEnd)) {
                break;
            }
            cursor = cursor.plusDays(interval);
        }

        return output;
    }

    private List<CalendarEventOccurrenceResponse> expandWeekly(CalendarEvent event, EventRecurrence recurrence, Instant rangeStart, Instant rangeEnd) {
        List<CalendarEventOccurrenceResponse> output = new ArrayList<>();
        Set<DayOfWeek> days = recurrence.getByWeekDays() == null || recurrence.getByWeekDays().isEmpty()
                ? Set.of(event.getTime().atZone(ZoneOffset.UTC).getDayOfWeek())
                : recurrence.getByWeekDays();

        List<DayOfWeek> sortedDays = new ArrayList<>(days);
        sortedDays.sort(Comparator.naturalOrder());

        ZonedDateTime anchor = event.getTime().atZone(ZoneOffset.UTC);
        LocalDate anchorWeekStart = anchor.toLocalDate().minusDays(anchor.getDayOfWeek().getValue() - 1L);
        int interval = safeInterval(recurrence);
        int generated = 0;
        long durationSeconds = occurrenceDurationSeconds(event);

        for (int weekIndex = 0; weekIndex < 5200; weekIndex++) {
            LocalDate weekStart = anchorWeekStart.plusWeeks((long) weekIndex * interval);
            for (DayOfWeek day : sortedDays) {
                LocalDate dayDate = weekStart.plusDays(day.getValue() - 1L);
                ZonedDateTime candidate = dayDate.atTime(anchor.toLocalTime()).atZone(ZoneOffset.UTC);
                Instant occurrenceStart = candidate.toInstant();

                if (occurrenceStart.isBefore(event.getTime())) {
                    continue;
                }

                if (!isWithinRecurrenceLimit(occurrenceStart, recurrence, generated)) {
                    return output;
                }

                generated++;
                Instant occurrenceEnd = durationSeconds >= 0 ? occurrenceStart.plusSeconds(durationSeconds) : null;
                if (intersects(rangeStart, rangeEnd, occurrenceStart, occurrenceEnd)) {
                    output.add(CalendarEventOccurrenceResponse.fromEventAndOccurrence(event, occurrenceStart, occurrenceEnd));
                }

                if (!occurrenceStart.isBefore(rangeEnd)) {
                    return output;
                }
            }
        }

        return output;
    }

    private List<CalendarEventOccurrenceResponse> expandMonthly(CalendarEvent event, EventRecurrence recurrence, Instant rangeStart, Instant rangeEnd) {
        List<CalendarEventOccurrenceResponse> output = new ArrayList<>();
        ZonedDateTime anchor = event.getTime().atZone(ZoneOffset.UTC);
        int interval = safeInterval(recurrence);
        Integer byMonthDayValue = recurrence.getByMonthDay();
        int dayOfMonth = byMonthDayValue == null ? anchor.getDayOfMonth() : byMonthDayValue.intValue();
        int generated = 0;
        long durationSeconds = occurrenceDurationSeconds(event);

        for (int monthIndex = 0; monthIndex < 2400; monthIndex++) {
            ZonedDateTime monthCandidate = anchor.plusMonths((long) monthIndex * interval);
            int maxDay = monthCandidate.toLocalDate().lengthOfMonth();
            if (dayOfMonth > maxDay) {
                continue;
            }

            ZonedDateTime candidate = monthCandidate.withDayOfMonth(dayOfMonth);
            Instant occurrenceStart = candidate.toInstant();

            if (!isWithinRecurrenceLimit(occurrenceStart, recurrence, generated)) {
                break;
            }

            generated++;
            Instant occurrenceEnd = durationSeconds >= 0 ? occurrenceStart.plusSeconds(durationSeconds) : null;
            if (intersects(rangeStart, rangeEnd, occurrenceStart, occurrenceEnd)) {
                output.add(CalendarEventOccurrenceResponse.fromEventAndOccurrence(event, occurrenceStart, occurrenceEnd));
            }

            if (!occurrenceStart.isBefore(rangeEnd)) {
                break;
            }
        }

        return output;
    }

    private List<CalendarEventOccurrenceResponse> expandYearly(CalendarEvent event, EventRecurrence recurrence, Instant rangeStart, Instant rangeEnd) {
        List<CalendarEventOccurrenceResponse> output = new ArrayList<>();
        ZonedDateTime cursor = event.getTime().atZone(ZoneOffset.UTC);
        int interval = safeInterval(recurrence);
        int generated = 0;
        long durationSeconds = occurrenceDurationSeconds(event);

        while (true) {
            Instant occurrenceStart = cursor.toInstant();
            if (!isWithinRecurrenceLimit(occurrenceStart, recurrence, generated)) {
                break;
            }

            generated++;
            Instant occurrenceEnd = durationSeconds >= 0 ? occurrenceStart.plusSeconds(durationSeconds) : null;
            if (intersects(rangeStart, rangeEnd, occurrenceStart, occurrenceEnd)) {
                output.add(CalendarEventOccurrenceResponse.fromEventAndOccurrence(event, occurrenceStart, occurrenceEnd));
            }

            if (!occurrenceStart.isBefore(rangeEnd)) {
                break;
            }
            cursor = cursor.plusYears(interval);
        }

        return output;
    }

    private boolean isWithinRecurrenceLimit(Instant candidateStart, EventRecurrence recurrence, int generatedSoFar) {
        if (recurrence.getUntil() != null && candidateStart.isAfter(recurrence.getUntil())) {
            return false;
        }
        Integer countLimit = recurrence.getCount();
        if (countLimit != null && generatedSoFar >= countLimit.intValue()) {
            return false;
        }
        return true;
    }

    private int safeInterval(EventRecurrence recurrence) {
        Integer interval = recurrence.getInterval();
        return interval == null || interval.intValue() <= 0 ? 1 : interval.intValue();
    }

    private long occurrenceDurationSeconds(CalendarEvent event) {
        if (event.getEndTime() == null) {
            return -1;
        }
        return ChronoUnit.SECONDS.between(event.getTime(), event.getEndTime());
    }

    private boolean intersects(Instant rangeStart, Instant rangeEnd, Instant occurrenceStart, Instant occurrenceEnd) {
        if (occurrenceEnd == null) {
            return !occurrenceStart.isBefore(rangeStart) && occurrenceStart.isBefore(rangeEnd);
        }

        return occurrenceStart.isBefore(rangeEnd) && occurrenceEnd.isAfter(rangeStart);
    }
}
