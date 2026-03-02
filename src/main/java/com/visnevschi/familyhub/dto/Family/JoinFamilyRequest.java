package com.visnevschi.familyhub.dto.Family;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinFamilyRequest(
        @NotBlank
        @Size(max = 64)
        String code
) {
}
