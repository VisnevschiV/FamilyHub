package com.visnevschi.familyhub.dto.Persona;

import com.visnevschi.familyhub.dbenitity.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record CreatePersonaRequest(
        @NotBlank
        @Size(max = 200)
        String name,

        @NotNull
        @Past
        java.time.LocalDate birthday,

        @NotNull
        Gender gender,

        @Size(max = 500)
        String avatarUrl
) {
}
