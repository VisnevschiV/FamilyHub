package com.visnevschi.familyhub.dto.UserAccount;

public record LoginResponse(
        String token,
        long expiresIn
) {}