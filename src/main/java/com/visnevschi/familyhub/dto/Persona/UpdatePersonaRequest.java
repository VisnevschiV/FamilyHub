package com.visnevschi.familyhub.dto.Persona;

import com.visnevschi.familyhub.utils.Gender;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record UpdatePersonaRequest(
        @Size(max = 200)
        String name,

        @Past
        java.time.LocalDate birthday,

        Gender gender,

        @Size(max = 500)
        String avatarUrl
) {
}
