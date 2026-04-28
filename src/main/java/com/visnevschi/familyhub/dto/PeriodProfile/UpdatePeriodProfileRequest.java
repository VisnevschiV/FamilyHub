package com.visnevschi.familyhub.dto.PeriodProfile;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdatePeriodProfileRequest(
        @Min(15)
        @Max(60)
        Integer cycleLengthDays,

        @Min(1)
        @Max(15)
        Integer periodLengthDays,

        Boolean predictionEnabled,

        LocalDate lastPeriodStartDate
) {
}
