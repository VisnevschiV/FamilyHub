package com.visnevschi.familyhub.service;

import com.visnevschi.familyhub.dbenitity.CalendarEvent;
import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.dto.event.EventCreateRequest;
import com.visnevschi.familyhub.repository.FamilyRepository;
import com.visnevschi.familyhub.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired // Add this to find people by ID
    private PersonRepository personRepository;

    public List<Family> findAllFamilies() {
        return familyRepository.findAll();
    }

    public Family createFamily(Family family) {
        return familyRepository.save(family);
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
