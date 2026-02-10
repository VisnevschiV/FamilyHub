package com.visnevschi.familyhub.dto.Persona;

import com.visnevschi.familyhub.dbenitity.Gender;

public record PersonaResponse(
        Long id,
        String name,
        java.time.LocalDate birthday,
        Gender gender,
        String avatarUrl
) {
}
