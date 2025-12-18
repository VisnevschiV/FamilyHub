package com.visnevschi.familyhub.dto.family;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinFamilyRequest(
        @NotBlank
        @Size(min = 6, max = 10)
        String joinCode
) {
}
