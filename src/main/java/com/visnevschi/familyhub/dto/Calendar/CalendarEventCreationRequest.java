package com.visnevschi.familyhub.dto.Calendar;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CalendarEventCreationRequest {

    @NotBlank
    private String title;

    @Size(max = 500)
    private String description;

    @NotNull
    private Instant time;

    private Instant endTime;

    private boolean allDayEvent;

    private final Set<Long> participants = new HashSet<>();

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getTime() {
        return time;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public boolean isAllDayEvent() {
        return allDayEvent;
    }

    public Set<Long> getParticipants() {
        return participants;
    }
}
