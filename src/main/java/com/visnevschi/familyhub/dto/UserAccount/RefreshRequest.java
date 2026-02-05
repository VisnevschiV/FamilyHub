package com.visnevschi.familyhub.dto.UserAccount;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank
        String refreshToken
) {}
