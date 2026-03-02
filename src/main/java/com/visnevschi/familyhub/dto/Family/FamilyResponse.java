package com.visnevschi.familyhub.dto.Family;

public record FamilyResponse(
        Long id,
        String name,
        long memberCount
) {
}
