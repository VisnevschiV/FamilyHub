package com.visnevschi.familyhub.dto.Persona;

import com.visnevschi.familyhub.dto.Family.FamilyResponse;
import com.visnevschi.familyhub.utils.Gender;

public record PersonaResponse(
        Long id,
        String name,
        java.time.LocalDate birthday,
        Gender gender,
        String avatarUrl,
        FamilyResponse family
) {
}
