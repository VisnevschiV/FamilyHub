package com.visnevschi.familyhub.service;

import com.visnevschi.familyhub.dbenitity.CalendarEvent;
import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.dbenitity.UserAccount;
import com.visnevschi.familyhub.dto.event.EventCreateRequest;
import com.visnevschi.familyhub.dto.event.EventResponse;
import com.visnevschi.familyhub.dto.person.PersonGeneralDto;
import com.visnevschi.familyhub.repository.FamilyRepository;
import com.visnevschi.familyhub.repository.PersonRepository;
import com.visnevschi.familyhub.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired // Add this to find people by ID
    private PersonRepository personRepository;
    @Autowired
    private UserAccountRepository userAccountRepository;

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 8;


    public List<Family> findAllFamilies() {
        return familyRepository.findAll();
    }

    @Transactional
    public Family createMyFamily(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        UserAccount account = userAccountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Person founder = account.getPerson();

        if (founder.getFamily() != null) {
            throw new IllegalStateException("You are already in a family");
        }

        Family family = new Family(founder.getName() + "'s Family");
        family.setJoinCode(generateUniqueJoinCode());
        founder.setFamily(family);

        familyRepository.save(family);
        personRepository.save(founder);

        return family;
    }

    @Transactional
    public Family joinFamily(String email, String joinCode) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (joinCode == null || joinCode.isBlank()) {
            throw new IllegalArgumentException("Join code is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        UserAccount account = userAccountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Person person = account.getPerson();

        if (person.getFamily() != null) {
            throw new IllegalStateException("You are already in a family");
        }

        Family family = familyRepository.findByJoinCode(joinCode.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid join code"));

        person.setFamily(family);
        personRepository.save(person);

        return family;
    }

    private String generateUniqueJoinCode() {
        Random random = new Random();
        String code;
        do {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (familyRepository.findByJoinCode(code).isPresent());
        return code;
    }

    public Family getByIdOrThrow(Long id) {
        return familyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Family not found with id " + id));
    }


    // This handles the logic of linking an Event to a Family
    public Family addEventToFamily(Long familyId, EventCreateRequest request) {
        Family family = getByIdOrThrow(familyId);

        CalendarEvent event = new CalendarEvent(
                request.title(),
                request.description(),
                request.dateTime()
        );

        family.addEvent(event);
        return familyRepository.save(family);
    }

    public Family addEventToFamily(Long familyId, CalendarEvent event) {
        return familyRepository.findById(familyId).map(family -> {
            family.addEvent(event);
            return familyRepository.save(family);
        }).orElseThrow(() -> new RuntimeException("Family not found with id " + familyId));
    }

    public Family getMyFamilyOrThrow(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        UserAccount account = userAccountRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Family family = account.getPerson().getFamily();
        if (family == null) {
            throw new RuntimeException("You are not in a family yet");
        }

        return family;
    }

    public Set<Person> getMyFamilyMembers(String email) {

        Family family = getMyFamilyOrThrow(email);

        return family.getMembers();
    }

    public Set<CalendarEvent> getMyFamilyEvents(String email){

        Family family = getMyFamilyOrThrow(email);

        return family.getEvents();
    }

    public void deleteFamily(Long id) {
        familyRepository.deleteById(id);
    }

    public void addMemberToFamily(long familyId, long personId) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new RuntimeException("Family not found"));

        Person member = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found"));

        addMemberToFamily(familyId, member); // Reuse logic below
    }

    public void addMemberToFamily(long familyId, Person member) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new RuntimeException("Family not found"));

        // 1. Link in Java (Both sides!)
        member.setFamily(family); // <--- THIS WAS MISSING
        family.addMember(member);

        // 2. Save
        familyRepository.save(family);
    }
}
