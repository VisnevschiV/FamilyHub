package com.visnevschi.familyhub.dto.UserAccount;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserAccountDto(

        @Email
        @NotBlank
        String email,
        @NotBlank
        @Size(min = 8, max = 100)
        String password
){}
