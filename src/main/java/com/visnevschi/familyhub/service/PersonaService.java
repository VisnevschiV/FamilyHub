package com.visnevschi.familyhub.service;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.dbenitity.Gender;
import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.dbenitity.UserAccount;
import com.visnevschi.familyhub.dto.Persona.CreatePersonaRequest;
import com.visnevschi.familyhub.dto.Persona.UpdatePersonaRequest;
import com.visnevschi.familyhub.exception.NotFoundException;
import com.visnevschi.familyhub.repository.PersonaRepository;
import com.visnevschi.familyhub.repository.UserAccountRepository;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final UserAccountRepository userAccountRepository;

    public PersonaService(PersonaRepository personaRepository,
                          UserAccountRepository userAccountRepository) {
        this.personaRepository = personaRepository;
        this.userAccountRepository = userAccountRepository;
    }

    public Persona createForUser(UserAccount userAccount, String name, LocalDate birthday, Gender gender) {
        if (personaRepository.existsByUserAccountId(userAccount.getId())) {
            throw new IllegalStateException("Persona already exists for this account");
        }

        Persona persona = new Persona(userAccount);
        persona.setName(name);
        persona.setBirthday(birthday);
        persona.setGender(gender);

        return personaRepository.save(persona);
    }

    public Persona createForEmail(String email, CreatePersonaRequest request) {
        UserAccount account = findAccountByEmail(email);
        if (personaRepository.existsByUserAccountId(account.getId())) {
            throw new IllegalStateException("Persona already exists for this account");
        }

        Persona persona = new Persona(account);
        persona.setName(request.name().trim());
        persona.setBirthday(request.birthday());
        persona.setGender(request.gender());
        persona.setAvatarUrl(request.avatarUrl());

        return personaRepository.save(persona);
    }

    public Persona getForEmail(String email) {
        UserAccount account = findAccountByEmail(email);
        return personaRepository.findByUserAccountId(account.getId())
                .orElseThrow(() -> new NotFoundException("Persona not found"));
    }

    public Persona updateForEmail(String email, UpdatePersonaRequest request) {
        Persona persona = getForEmail(email);

        if (request.name() != null) {
            persona.setName(request.name().trim());
        }
        if (request.birthday() != null) {
            persona.setBirthday(request.birthday());
        }
        if (request.gender() != null) {
            persona.setGender(request.gender());
        }
        if (request.avatarUrl() != null) {
            persona.setAvatarUrl(request.avatarUrl());
        }

        return personaRepository.save(persona);
    }

    private UserAccount findAccountByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return userAccountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("User account not found"));
    }
}
