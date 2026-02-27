package com.visnevschi.familyhub.dto.Calendar;

import java.time.Instant;

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

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getTime() {
        return time;
    }
}
