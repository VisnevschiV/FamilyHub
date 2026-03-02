package com.visnevschi.familyhub.dto;

import java.time.Instant;
import java.util.Map;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String errorId,
        Map<String, String> details
) {
}
