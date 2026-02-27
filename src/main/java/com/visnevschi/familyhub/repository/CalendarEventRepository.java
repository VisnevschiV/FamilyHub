package com.visnevschi.familyhub.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.visnevschi.familyhub.document.CalendarEvent;

@Repository
public interface CalendarEventRepository extends MongoRepository<CalendarEvent, String> {
    java.util.List<CalendarEvent> findByFamilyId(Long familyId);
}
