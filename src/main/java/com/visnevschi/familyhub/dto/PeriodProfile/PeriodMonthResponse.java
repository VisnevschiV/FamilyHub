package com.visnevschi.familyhub.dto.PeriodProfile;

import java.util.List;

public record PeriodMonthResponse(
        Integer year,
        Integer month,
        List<PeriodRecordResponse> records,
        PeriodRecordResponse prediction
) {
}
