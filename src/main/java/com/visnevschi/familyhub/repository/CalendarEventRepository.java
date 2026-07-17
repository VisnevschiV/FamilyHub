package com.visnevschi.familyhub.repository;

import java.time.Instant;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.document.CalendarEvent;

@Repository
public interface CalendarEventRepository extends MongoRepository<CalendarEvent, String> {
    java.util.List<CalendarEvent> findByFamilyIdOrderByTimeAsc(Long familyId);
    
    // Find all events where time is between startTime and endTime
    // Used by EventReminderScheduler to find upcoming events
    java.util.List<CalendarEvent> findByTimeBetween(Instant startTime, Instant endTime);
}
