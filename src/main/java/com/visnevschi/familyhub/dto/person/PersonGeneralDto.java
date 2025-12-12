package com.visnevschi.familyhub.dto.person;

public record PersonGeneralDto(
        Long id,
        String name,
        String role,
        String email
) {
}