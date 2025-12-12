package com.visnevschi.familyhub.service;

import com.visnevschi.familyhub.dbenitity.CalendarEvent;
import com.visnevschi.familyhub.repository.CalendarEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private CalendarEventRepository repository;

    public CalendarEvent findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public List<CalendarEvent> findAll() {
        return repository.findAll();
    }

    public CalendarEvent save(CalendarEvent event) {
        return repository.save(event);
    }
}