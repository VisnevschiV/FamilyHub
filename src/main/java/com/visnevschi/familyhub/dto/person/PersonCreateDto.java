package com.visnevschi.familyhub.dto.person;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PersonCreateDto(
        @NotBlank
        @Size(max = 200)
        String name,

        @NotBlank
        @Size(max = 100)
        String role,

        @NotBlank
        @Email
        @Size(max = 320)
        String email
) {
}