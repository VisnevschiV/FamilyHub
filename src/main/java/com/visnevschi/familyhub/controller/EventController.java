package com.visnevschi.familyhub.controller;

import com.visnevschi.familyhub.dto.Mapper;
import com.visnevschi.familyhub.dto.event.EventCreateRequest;
import com.visnevschi.familyhub.dto.event.EventResponse;
import com.visnevschi.familyhub.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events") // This prefixes all URLs below with /events
public class EventController {

    private final EventService service;
    private final Mapper mapper;

    public EventController(Mapper mapper, EventService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping
    public List<EventResponse> getAllEvents() {
        return mapper.toResponse(service.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody EventCreateRequest event) {
        return mapper.toResponse(service.save(mapper.toEntity(event)));
    }
}
