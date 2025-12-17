package com.visnevschi.familyhub.dto.UserAccount;

public record UserDataDto(
        Long id,
        String email,
        String name,
        String role,
        Long familyId
) {
}
