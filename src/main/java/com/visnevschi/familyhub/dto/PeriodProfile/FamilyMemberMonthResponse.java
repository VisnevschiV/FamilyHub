package com.visnevschi.familyhub.dto.PeriodProfile;

import java.util.List;

public record FamilyMemberMonthResponse(
        Long personaId,
        Integer year,
        Integer month,
        List<PeriodRecordResponse> records,
        PeriodRecordResponse prediction
) {
}
