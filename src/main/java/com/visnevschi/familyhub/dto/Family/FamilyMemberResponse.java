package com.visnevschi.familyhub.dto.Family;

import com.visnevschi.familyhub.utils.Gender;

public record FamilyMemberResponse(
        Long id,
        String name,
        Gender gender
) {
}
