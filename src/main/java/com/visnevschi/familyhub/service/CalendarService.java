package com.visnevschi.familyhub.service;

import java.time.Instant;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.document.CalendarEvent;
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

    public CalendarEvent createEvent(String userEmail, String title, String description, Instant time, Instant endTime, boolean allDayEvent, java.util.Set<Long> participants) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);

        validateEventTiming(time, endTime, allDayEvent);

        CalendarEvent event = new CalendarEvent(title, description, time, allDayEvent ? null : endTime, allDayEvent, familyId);
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
        return calendarEventRepository.findByFamilyId(familyId);
    }

    public CalendarEvent updateEvent(String userEmail, String eventId, String title, String description, Instant time, Instant endTime, boolean allDayEvent, java.util.Set<Long> participants) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);

        CalendarEvent event = calendarEventRepository.findById(Objects.requireNonNull(eventId))
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getFamilyId().equals(familyId)) {
            throw new IllegalStateException("User does not have permission to update this event");
        }

        validateEventTiming(time, endTime, allDayEvent);

        event.setTitle(title);
        event.setDescription(description);
        event.setTime(time);
        event.setEndTime(allDayEvent ? null : endTime);
        event.setAllDayEvent(allDayEvent);
        event.setParticipants(participants != null ? new java.util.HashSet<>(participants) : new java.util.HashSet<>());
        return calendarEventRepository.save(event);
    }

    private void validateEventTiming(Instant time, Instant endTime, boolean allDayEvent) {
        if (time == null) {
            throw new IllegalArgumentException("Event start time is required");
        }

        if (allDayEvent) {
            return;
        }

        if (endTime != null && endTime.isBefore(time)) {
            throw new IllegalArgumentException("Event end time must be after start time");
        }
    }
}
