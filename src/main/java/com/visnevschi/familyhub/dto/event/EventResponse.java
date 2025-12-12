package com.visnevschi.familyhub.dto.event;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDateTime dateTime
) {
}