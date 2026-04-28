package com.visnevschi.familyhub.dto.PeriodProfile;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record PeriodDateRequest(
        @NotNull
        @PastOrPresent
        LocalDate date
) {
}
