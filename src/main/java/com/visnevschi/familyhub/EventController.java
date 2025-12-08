package com.visnevschi.familyhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events") // This prefixes all URLs below with /events
public class EventController {

    @Autowired
    private CalendarEventRepository repository;

    @GetMapping
    public List<CalendarEvent> getAllEvents() {
        return repository.findAll();
    }

    @PostMapping
    public CalendarEvent createEvent(@RequestBody CalendarEvent event) {
        return repository.save(event);
    }
}
