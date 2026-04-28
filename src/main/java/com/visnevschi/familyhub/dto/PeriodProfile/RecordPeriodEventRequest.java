package com.visnevschi.familyhub.dto.PeriodProfile;

import java.time.LocalDate;

import com.visnevschi.familyhub.utils.PeriodEventType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record RecordPeriodEventRequest(
        @NotNull
        PeriodEventType eventType,

        @NotNull
        @PastOrPresent
        LocalDate date
) {
}
