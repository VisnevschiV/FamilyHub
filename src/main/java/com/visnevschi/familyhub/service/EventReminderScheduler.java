package com.visnevschi.familyhub.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.document.CalendarEvent;
import com.visnevschi.familyhub.repository.CalendarEventRepository;

@Service
public class EventReminderScheduler {
    private static final Logger log = LoggerFactory.getLogger(EventReminderScheduler.class);
    private static final long REMINDER_WINDOW_MINUTES = 10;
    
    // Track which reminders have been sent (eventId:personaId)
    // Prevents sending duplicate reminders if scheduler runs multiple times
    private final Set<String> sentReminders = ConcurrentHashMap.newKeySet();

    private final CalendarEventRepository calendarEventRepository;
    private final NotificationService notificationService;

    public EventReminderScheduler(CalendarEventRepository calendarEventRepository,
                                  NotificationService notificationService) {
        this.calendarEventRepository = calendarEventRepository;
        this.notificationService = notificationService;
    }

    /**
     * Scheduled to run every 10 minutes.
     * Checks for upcoming events and sends notifications to all participants.
     * 
     * Flow:
     * 1. Calculate time window (now to now+10 min)
     * 2. Query DB for events in that window
     * 3. For each event, notify all participants
     * 4. Use dedup set to avoid sending same reminder twice
     */
    @Scheduled(fixedRate = 300000)  // 300,000 milliseconds = 5 minutes
    public void checkUpcomingEvents() {
        log.info("=== Starting upcoming event reminder check ===");
        
        Instant now = Instant.now();
        Instant windowEnd = now.plus(REMINDER_WINDOW_MINUTES, ChronoUnit.MINUTES);
        
        log.info("Searching for events between {} and {}", now, windowEnd);
        
        // Ask repository for all events happening in next 10 minutes
        List<CalendarEvent> upcomingEvents = calendarEventRepository.findByTimeBetween(now, windowEnd);
        log.info("Found {} upcoming events", upcomingEvents.size());
        
        // Process each event
        for (CalendarEvent event : upcomingEvents) {
            log.debug("Processing event: id={}, title={}, time={}", event.getId(), event.getTitle(), event.getTime());
            
            // Notify each participant individually
            if (event.getParticipants() == null || event.getParticipants().isEmpty()) {
                log.debug("Event {} has no participants, skipping", event.getId());
                continue;
            }
            
            for (Long participantId : event.getParticipants()) {
                // Create unique key for this reminder
                String reminderKey = event.getId() + ":" + participantId;
                
                // Only send if we haven't already sent this reminder
                if (!sentReminders.contains(reminderKey)) {
                    long minutesUntilEvent = ChronoUnit.MINUTES.between(Instant.now(), event.getTime());
                    String message = "Upcoming event in " + minutesUntilEvent + " minutes: " + event.getTitle();
                    
                    // This is async, so caller (scheduler) won't block
                    notificationService.createNotification(participantId, message);
                    
                    sentReminders.add(reminderKey);
                    log.info("Sent reminder - event={}, participant={}", event.getId(), participantId);
                } else {
                    log.debug("Reminder already sent for event={} to participant={}", event.getId(), participantId);
                }
            }
        }
        
        log.info("=== Completed upcoming event reminder check ===");
    }
}
