package com.visnevschi.familyhub.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.document.CalendarEvent;
import com.visnevschi.familyhub.exception.NotFoundException;
import com.visnevschi.familyhub.repository.CalendarEventRepository;

@Service
public class CalendarService {
    private final FamilyService familyService;
    private final CalendarEventRepository calendarEventRepository;

    public CalendarService(FamilyService familyService, CalendarEventRepository calendarEventRepository) {
        this.familyService = familyService;
        this.calendarEventRepository = calendarEventRepository;
    }

    public void createEvent(String userEmail, String title, String description, java.time.Instant time) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);

        CalendarEvent event = new CalendarEvent(title, description, time, familyId);
        calendarEventRepository.save(event);
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

    public void updateEvent(String userEmail, String eventId, String title, String description, java.time.Instant time) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);

        CalendarEvent event = calendarEventRepository.findById(Objects.requireNonNull(eventId))
                .orElseThrow(() -> new NotFoundException("Event not found"));

        if (!event.getFamilyId().equals(familyId)) {
            throw new IllegalStateException("User does not have permission to update this event");
        }

        event.setTitle(title);
        event.setDescription(description);
        event.setTime(time);
        calendarEventRepository.save(event);
    }
}
