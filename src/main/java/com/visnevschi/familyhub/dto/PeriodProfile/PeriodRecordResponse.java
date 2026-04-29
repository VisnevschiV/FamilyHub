package com.visnevschi.familyhub.dto.PeriodProfile;

import java.time.LocalDate;

public record PeriodRecordResponse(
        String id,
        LocalDate startDate,
        LocalDate endDate,
        boolean predicted
) {
}
