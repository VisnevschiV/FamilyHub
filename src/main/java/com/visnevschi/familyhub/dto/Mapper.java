package com.visnevschi.familyhub.dto;

import com.visnevschi.familyhub.dbenitity.CalendarEvent;
import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.dto.event.EventCreateRequest;
import com.visnevschi.familyhub.dto.event.EventResponse;
import com.visnevschi.familyhub.dto.family.FamilyDetailsResponse;
import com.visnevschi.familyhub.dto.person.PersonGeneralDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class Mapper {

    public FamilyDetailsResponse toDetails(Family family) {
        List<PersonGeneralDto> participants = family.getMembers().stream()
                .map(this::toGeneral)
                .sorted(Comparator.comparing(PersonGeneralDto::id, Comparator.nullsLast(Long::compareTo)))
                .toList();

        List<EventResponse> events = family.getEvents().stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(EventResponse::dateTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        return new FamilyDetailsResponse(
                family.getId(),
                family.getName(),
                participants,
                events
        );
    }

    public PersonGeneralDto toGeneral(Person person) {
        return new PersonGeneralDto(
                person.getId(),
                person.getName(),
                person.getRole(),
                person.getEmail()
        );
    }

    public EventResponse toResponse(CalendarEvent event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDateTime()
        );
    }

    public CalendarEvent toEntity(EventCreateRequest request) {
        return new CalendarEvent(
                request.title(),
                request.description(),
                request.dateTime()
        );
    }
}