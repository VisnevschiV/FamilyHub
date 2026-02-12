package com.visnevschi.familyhub.dto.UserAccount;

import com.visnevschi.familyhub.utils.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
        @NotBlank
        @Email
        @Size(max = 320)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        @Size(max = 200)
        String name,

        @NotNull
        @Past
        java.time.LocalDate birthday,

        @NotNull
        Gender gender
){
}
