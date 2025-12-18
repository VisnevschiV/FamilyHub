package com.visnevschi.familyhub.dto;

import com.visnevschi.familyhub.dbenitity.CalendarEvent;
import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.dto.event.EventCreateRequest;
import com.visnevschi.familyhub.dto.event.EventResponse;
import com.visnevschi.familyhub.dto.family.FamilyDetailsResponse;
import com.visnevschi.familyhub.dto.person.PersonCreateDto;
import com.visnevschi.familyhub.dto.person.PersonGeneralDto;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Mapper {

    public FamilyDetailsResponse toDetails(Family family) {
        List<PersonGeneralDto> participants = family.getMembers().stream()
                .map(this::toGeneral)
                .toList();

        List<EventResponse> events = family.getEvents().stream()
                .map(this::toResponse)
                .sorted(Comparator.comparing(EventResponse::dateTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        return new FamilyDetailsResponse(
                family.getId(),
                family.getName(),
                family.getJoinCode(),
                participants,
                events
        );
    }

    public PersonGeneralDto toGeneral(Person person) {
        return new PersonGeneralDto(
                person.getName(),
                person.getRole()
        );
    }

    public Set<PersonGeneralDto> toGeneral(Set<Person> people) {
        if (people == null) {
            return Set.of();
        }
        return people.stream()
                .map(this::toGeneral)
                .collect(Collectors.toSet());
    }

    public Person toEntity(PersonGeneralDto dto) {
        return new Person(dto.name(), dto.role());
    }

    public Person toEntity(PersonCreateDto dto) {
        return new Person(dto.name(), dto.role());
    }

    public EventResponse toResponse(CalendarEvent event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDateTime()
        );
    }

    public List<EventResponse> toResponse(List<CalendarEvent> events) {
        return events.stream().map(this::toResponse).toList();
    }

    public CalendarEvent toEntity(EventCreateRequest request) {
        return new CalendarEvent(
                request.title(),
                request.description(),
                request.dateTime()
        );
    }


}