package com.visnevschi.familyhub.dto.UserAccount;

public record AuthTokens(
        String accessToken,
        long ttlSeconds,
        String refreshToken,
        long refreshTtlSeconds
) {}
