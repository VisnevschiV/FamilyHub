package com.visnevschi.familyhub.dto.notifications;

import java.time.Instant;

public record NotificationResponse(
	String id,
	String message,
    Instant createdAt,
	boolean read
) {
}
