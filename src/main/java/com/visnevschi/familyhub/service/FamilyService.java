package com.visnevschi.familyhub.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;

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

@Service
@Transactional
public class FamilyService {

    private static final int CODE_BYTES = 18;

    private final PersonaRepository personaRepository;
    private final FamilyRepository familyRepository;
    private final FamilyInviteRepository familyInviteRepository;
    private final long joinCodeTtlSeconds;
    private final SecureRandom secureRandom = new SecureRandom();

    public FamilyService(PersonaRepository personaRepository,
                         FamilyRepository familyRepository,
                         FamilyInviteRepository familyInviteRepository,
                         @Value("${app.family.join-code-ttl-seconds:900}") long joinCodeTtlSeconds) {
        this.personaRepository = personaRepository;
        this.familyRepository = familyRepository;
        this.familyInviteRepository = familyInviteRepository;
        this.joinCodeTtlSeconds = joinCodeTtlSeconds;
    }

    public Family createFamily(String email, String name) {
        Persona persona = getPersonaForEmail(email);
        if (persona.getFamily() != null) {
            throw new IllegalStateException("Persona already belongs to a family");
        }

        Family family = new Family(name.trim());
        familyRepository.save(family);

        family.addMember(persona);
        personaRepository.save(persona);

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
        personaRepository.save(persona);

        return invite.getFamily();
    }

    public FamilyInvite generateJoinCode(String email) {
        Persona persona = getPersonaForEmail(email);
        Family family = requireFamily(persona);

        familyInviteRepository.deleteByFamilyId(family.getId());

        String code = generateUniqueCode();
        Instant expiresAt = Instant.now().plusSeconds(joinCodeTtlSeconds);

        FamilyInvite invite = new FamilyInvite(code, expiresAt, family);
        return familyInviteRepository.save(invite);
    }

    public void leaveFamily(String email) {
        Persona persona = getPersonaForEmail(email);
        Family family = requireFamily(persona);

        family.removeMember(persona);
        personaRepository.save(persona);
        personaRepository.flush();

        long remainingMembers = personaRepository.countByFamilyId(family.getId());
        if (remainingMembers == 0) {
            familyInviteRepository.deleteByFamilyId(family.getId());
            familyRepository.delete(family);
        }
    }

    public Family updateFamilyName(String email, String newName) {
        Persona persona = getPersonaForEmail(email);
        Family family = requireFamily(persona);

        family.setName(newName.trim());
        return familyRepository.save(family);
    }

    public long countMembers(Long familyId) {
        return personaRepository.countByFamilyId(familyId);
    }

    private Persona getPersonaForEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return personaRepository.findByUserAccountEmail(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("Persona not found"));
    }

    private Family requireFamily(Persona persona) {
        Family family = persona.getFamily();
        if (family == null) {
            throw new IllegalStateException("Persona does not belong to a family");
        }
        return family;
    }

    private String generateUniqueCode() {
        String code = generateCode();
        while (familyInviteRepository.findByCode(code).isPresent()) {
            code = generateCode();
        }
        return code;
    }

    private String generateCode() {
        byte[] bytes = new byte[CODE_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
