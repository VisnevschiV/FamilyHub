package com.visnevschi.familyhub.controller;

import com.visnevschi.familyhub.dto.event.EventCreateRequest;
import com.visnevschi.familyhub.dto.family.FamilyDetailsResponse;
import com.visnevschi.familyhub.dto.Mapper;
import com.visnevschi.familyhub.service.FamilyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/families")
public class FamilyController {

    private final FamilyService familyService;
    private final Mapper mapper;

    public FamilyController(FamilyService familyService, Mapper mapper) {
        this.familyService = familyService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public FamilyDetailsResponse getFamilyById(@PathVariable Long id) {
        return mapper.toDetails(familyService.getByIdOrThrow(id));
    }

    @PostMapping("/{id}/events")
    public FamilyDetailsResponse addEventToFamily(@PathVariable Long id, @Valid @RequestBody EventCreateRequest request) {
        return mapper.toDetails(familyService.addEventToFamily(id, request));
    }

    @PostMapping("/{familyId}/members/{personId}")
    public FamilyDetailsResponse addMemberToFamily(@PathVariable long familyId, @PathVariable long personId) {
        familyService.addMemberToFamily(familyId, personId);
        return mapper.toDetails(familyService.getByIdOrThrow(familyId));
    }
}