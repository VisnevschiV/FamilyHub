package com.visnevschi.familyhub;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {
    // We can add custom queries later, e.g.:
    // List<FamilyEvent> findByTitle(String title);
}
