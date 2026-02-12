package com.visnevschi.familyhub.dto.Family;

import java.time.Instant;

public record JoinCodeResponse(
        String code,
        Instant expiresAt
) {
}
