package com.visnevschi.familyhub.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.FamilyInvite;
import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.dto.Family.CreateFamilyRequest;
import com.visnevschi.familyhub.dto.Family.FamilyMemberResponse;
import com.visnevschi.familyhub.dto.Family.FamilyResponse;
import com.visnevschi.familyhub.dto.Family.JoinCodeResponse;
import com.visnevschi.familyhub.dto.Family.JoinFamilyRequest;
import com.visnevschi.familyhub.dto.Family.UpdateFamilyRequest;
import com.visnevschi.familyhub.service.FamilyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/families")
public class FamilyController {

    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FamilyResponse create(@AuthenticationPrincipal Jwt jwt,
                                 @Valid @RequestBody CreateFamilyRequest request) {
        Family family = familyService.createFamily(jwt.getSubject(), request.name());
        return toResponse(family);
    }

    @PostMapping("/join")
    public FamilyResponse join(@AuthenticationPrincipal Jwt jwt,
                               @Valid @RequestBody JoinFamilyRequest request) {
        Family family = familyService.joinFamily(jwt.getSubject(), request.code());
        return toResponse(family);
    }

    @PostMapping("/me/join-code")
    public JoinCodeResponse generateJoinCode(@AuthenticationPrincipal Jwt jwt) {
        FamilyInvite invite = familyService.generateJoinCode(jwt.getSubject());
        return new JoinCodeResponse(invite.getCode(), invite.getExpiresAt());
    }

    @PatchMapping("/me")
    public FamilyResponse updateName(@AuthenticationPrincipal Jwt jwt,
                                     @Valid @RequestBody UpdateFamilyRequest request) {
        Family family = familyService.updateFamilyName(jwt.getSubject(), request.name());
        return toResponse(family);
    }

    @PostMapping("/me/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leave(@AuthenticationPrincipal Jwt jwt) {
        familyService.leaveFamily(jwt.getSubject());
    }

    @GetMapping("/me/members")
    public List<FamilyMemberResponse> members(@AuthenticationPrincipal Jwt jwt) {
        return familyService.getFamilyMembersForUser(jwt.getSubject()).stream()
                .map(this::toMemberResponse)
                .toList();
    }

    private FamilyResponse toResponse(Family family) {
        long members = familyService.countMembers(family.getId());
        return new FamilyResponse(family.getId(), family.getName(), members);
    }

    private FamilyMemberResponse toMemberResponse(Persona persona) {
        return new FamilyMemberResponse(persona.getId(), persona.getName(), persona.getGender());
    }
}
