package com.visnevschi.familyhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.dto.Calendar.CalendarEventCreationRequest;
import com.visnevschi.familyhub.document.CalendarEvent;
import com.visnevschi.familyhub.service.CalendarService;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void createEvent(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CalendarEventCreationRequest request) {
        calendarService.createEvent(jwt.getSubject(), request.getTitle(), request.getDescription(), request.getTime());
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@AuthenticationPrincipal Jwt jwt, @PathVariable String eventId) {
        calendarService.deleteEvent(jwt.getSubject(), eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEvent(@AuthenticationPrincipal Jwt jwt, @PathVariable String eventId, @Valid @RequestBody CalendarEventCreationRequest request) {
        calendarService.updateEvent(jwt.getSubject(), eventId, request.getTitle(), request.getDescription(), request.getTime());
    }

    @GetMapping("")
    public List<CalendarEvent> getEvents(@AuthenticationPrincipal Jwt jwt) {
        return calendarService.getEventsForFamily(jwt.getSubject());
    }
}
