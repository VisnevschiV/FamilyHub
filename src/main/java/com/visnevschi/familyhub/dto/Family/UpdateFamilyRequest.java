package com.visnevschi.familyhub.dto.Family;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFamilyRequest(
        @NotBlank
        @Size(max = 200)
        String name
) {
}
