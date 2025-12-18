package com.visnevschi.familyhub.controller;

import com.visnevschi.familyhub.dbenitity.CalendarEvent;
import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.dto.event.EventResponse;
import com.visnevschi.familyhub.dto.family.FamilyDetailsResponse;
import com.visnevschi.familyhub.dto.Mapper;
import com.visnevschi.familyhub.dto.family.JoinFamilyRequest;
import com.visnevschi.familyhub.dto.person.PersonGeneralDto;
import com.visnevschi.familyhub.service.FamilyService;
import com.visnevschi.familyhub.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/families")
public class FamilyController {

    private final FamilyService familyService;
    private final PersonService personService;
    private final Mapper mapper;

    public FamilyController(FamilyService familyService, PersonService personService, Mapper mapper) {
        this.familyService = familyService;
        this.personService = personService;
        this.mapper = mapper;
    }

    @GetMapping("/myFamily")
    public FamilyDetailsResponse getFamilyByLoggedUser(@AuthenticationPrincipal Jwt jwt) {
        try {
            return mapper.toDetails(familyService.getMyFamilyOrThrow(jwt.getSubject()));
        } catch (RuntimeException ex) {
            if ("You are not in a family yet".equals(ex.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
            }
            throw ex;
        }
    }

    @GetMapping("/myFamily/members")
    public List<PersonGeneralDto> getFamilyMembers(@AuthenticationPrincipal Jwt jwt) {
        try {
            return familyService.getMyFamilyMembers(jwt.getSubject())
                    .stream()
                    .map(mapper::toGeneral)
                    .toList();
        } catch (RuntimeException ex) {
            if ("You are not in a family yet".equals(ex.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
            }
            throw ex;
        }
    }


    @GetMapping("/myFamily/events")
    public List<EventResponse> getFamilyEvents(@AuthenticationPrincipal Jwt jwt) {
        try {
            return familyService.getMyFamilyEvents(jwt.getSubject())
                    .stream()
                    .map(mapper::toResponse)
                    .toList();
        } catch (RuntimeException ex) {
            if ("You are not in a family yet".equals(ex.getMessage())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
            }
            throw ex;
        }
    }

    @GetMapping("/myFamily/joinCode")
    public Map<String, String> getMyFamilyJoinCode(@AuthenticationPrincipal Jwt jwt) {
        Family family = familyService.getMyFamilyOrThrow(jwt.getSubject());
        return Map.of("joinCode", family.getJoinCode());
    }

    @PostMapping("/create")
    public FamilyDetailsResponse createFamily(@AuthenticationPrincipal Jwt jwt){
        return mapper.toDetails(familyService.createMyFamily(jwt.getSubject()));
    }

    @PostMapping("/join")
    public FamilyDetailsResponse joinFamily(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody JoinFamilyRequest request) {
        return mapper.toDetails(familyService.joinFamily(jwt.getSubject(), request.joinCode()));
    }


}