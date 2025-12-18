package com.visnevschi.familyhub.dto.family;

import com.visnevschi.familyhub.dto.event.EventResponse;
import com.visnevschi.familyhub.dto.person.PersonGeneralDto;

import java.util.List;

public record FamilyDetailsResponse(
        Long id,
        String name,
        String joinCode,
        List<PersonGeneralDto> participants,
        List<EventResponse> events
) {
}