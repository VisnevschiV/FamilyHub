package com.visnevschi.familyhub.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.FamilyInvite;
import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.exception.NotFoundException;
import com.visnevschi.familyhub.repository.FamilyInviteRepository;
import com.visnevschi.familyhub.repository.FamilyRepository;
import com.visnevschi.familyhub.repository.PersonaRepository;
import com.visnevschi.familyhub.service.CodeService;

@Service
@Transactional
public class FamilyService {

    private final PersonaRepository personaRepository;
    private final FamilyRepository familyRepository;
    private final FamilyInviteRepository familyInviteRepository;
    private final NotificationService notificationService;
    private final CodeService codeService;



    public FamilyService(PersonaRepository personaRepository,
                         FamilyRepository familyRepository,
                         FamilyInviteRepository familyInviteRepository,
                         NotificationService notificationService,
                         CodeService codeService) {
        this.personaRepository = personaRepository;
        this.familyRepository = familyRepository;
        this.familyInviteRepository = familyInviteRepository;
        this.notificationService = notificationService;
        this.codeService = codeService;
    }

    public Family createFamily(String email, String name) {
        Persona persona = getPersonaForEmail(email);
        if (persona.getFamily() != null) {
            throw new IllegalStateException("Persona already belongs to a family");
        }

        Family family = new Family(name.trim());
        familyRepository.save(family);

        family.addMember(persona);
        personaRepository.save(Objects.requireNonNull(persona));
        notificationService.createNotification(family.getId(), "Family created: " + name);
        return family;
    }

    public Family joinFamily(String email, String code) {
        Persona persona = getPersonaForEmail(email);
        if (persona.getFamily() != null) {
            throw new IllegalStateException("Persona already belongs to a family");
        }

        FamilyInvite invite = familyInviteRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired family code"));

        if (Instant.now().isAfter(invite.getExpiresAt())) {
            familyInviteRepository.delete(invite);
            throw new IllegalArgumentException("Invalid or expired family code");
        }

        invite.getFamily().addMember(persona);
        personaRepository.save(Objects.requireNonNull(persona));
        notificationService.createNotification(invite.getFamily().getId(), "New member joined the family: " + persona.getName());
        return invite.getFamily();
    }

    public FamilyInvite generateJoinCode(String email) {
        Persona persona = getPersonaForEmail(email);
        Family family = requireFamily(persona);

        familyInviteRepository.deleteByFamilyId(family.getId());
        FamilyInvite invite = new FamilyInvite();
        invite.setFamily(family);
        codeService.generateUniqueCode(familyInviteRepository, invite);//code and expires are set insite invite

        return familyInviteRepository.save(invite);
    }

    public void leaveFamily(String email) {
        Persona persona = getPersonaForEmail(email);
        Family family = requireFamily(persona);

        family.removeMember(persona);
        personaRepository.save(Objects.requireNonNull(persona));
        personaRepository.flush();

        long remainingMembers = personaRepository.countByFamilyId(family.getId());
        if (remainingMembers == 0) {
            familyInviteRepository.deleteByFamilyId(family.getId());
            familyRepository.delete(family);
        }
        notificationService.createNotification(family.getId(), "A member has left the family: " + persona.getName());
    }

    public Family updateFamilyName(String email, String newName) {
        Persona persona = getPersonaForEmail(email);
        Family family = requireFamily(persona);

        family.setName(newName.trim());
        Family updatedFamily = familyRepository.save(family);
        notificationService.createNotification(updatedFamily.getId(), "Family name updated to: " + newName);
        return updatedFamily;
    }

    public long countMembers(Long familyId) {
        return personaRepository.countByFamilyId(familyId);
    }

    //TODO: this belongs in PersonaService
    private Persona getPersonaForEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        Persona persona = personaRepository.findByUserAccountEmail(normalizedEmail)
            .orElseThrow(() -> new NotFoundException("Persona not found"));
        return Objects.requireNonNull(persona);
    }

    private Family requireFamily(Persona persona) {
        Family family = persona.getFamily();
        if (family == null) {
            throw new IllegalStateException("Persona does not belong to a family");
        }
        return family;
    }


    public Long getFamilyIdForUser(String email) {
        Persona persona = getPersonaForEmail(email);
        Family family = requireFamily(persona);
        return family.getId();
    }

    public List<Persona> getFamilyMembersForUser(String email) {
        Long familyId = getFamilyIdForUser(email);
        return personaRepository.findByFamilyIdOrderByNameAsc(familyId);
    }
}
