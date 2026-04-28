package com.visnevschi.familyhub.dto.PeriodProfile;

import java.time.LocalDate;

public record PeriodProfileResponse(
        Long personaId,
        Integer cycleLengthDays,
        Integer effectiveCycleLengthDays,
        Integer periodLengthDays,
        Boolean predictionEnabled,
        LocalDate lastPeriodStartDate,
        LocalDate lastPeriodEndDate,
        Integer learnedCycleLengthDays,
        Integer learningSamples,
        LocalDate nextPredictedStartDate
) {
}
