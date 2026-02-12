package com.visnevschi.familyhub.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.dto.Family.FamilyResponse;
import com.visnevschi.familyhub.dto.Persona.CreatePersonaRequest;
import com.visnevschi.familyhub.dto.Persona.PersonaResponse;
import com.visnevschi.familyhub.dto.Persona.UpdatePersonaRequest;
import com.visnevschi.familyhub.service.FamilyService;
import com.visnevschi.familyhub.service.PersonaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/personas")
public class PersonaController {

    private final PersonaService personaService;
    private final FamilyService familyService;

    public PersonaController(PersonaService personaService,
                             FamilyService familyService) {
        this.personaService = personaService;
        this.familyService = familyService;
    }

    @PostMapping("/me")
    public PersonaResponse create(@AuthenticationPrincipal Jwt jwt,
                                  @Valid @RequestBody CreatePersonaRequest request) {
        Persona persona = personaService.createForEmail(jwt.getSubject(), request);
        return toResponse(persona);
    }

    @GetMapping("/me")
    public PersonaResponse me(@AuthenticationPrincipal Jwt jwt) {
        Persona persona = personaService.getForEmail(jwt.getSubject());
        return toResponse(persona);
    }

    @PatchMapping("/me")
    public PersonaResponse update(@AuthenticationPrincipal Jwt jwt,
                                  @Valid @RequestBody UpdatePersonaRequest request) {
        Persona persona = personaService.updateForEmail(jwt.getSubject(), request);
        return toResponse(persona);
    }

    private PersonaResponse toResponse(Persona persona) {
        FamilyResponse familyResponse = null;
        Family family = persona.getFamily();
        if (family != null) {
            long members = familyService.countMembers(family.getId());
            familyResponse = new FamilyResponse(family.getId(), family.getName(), members);
        }

        return new PersonaResponse(
                persona.getId(),
                persona.getName(),
                persona.getBirthday(),
                persona.getGender(),
                persona.getAvatarUrl(),
                familyResponse
        );
    }
}
