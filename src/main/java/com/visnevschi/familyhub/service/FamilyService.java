package com.visnevschi.familyhub.service;

import com.visnevschi.familyhub.dbenitity.CalendarEvent;
import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.repository.FamilyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyService {

    @Autowired
    private FamilyRepository familyRepository;


    public List<Family> findAllFamilies() {
        return familyRepository.findAll();
    }

    public Family createFamily(Family family) {
        return familyRepository.save(family);
    }

    // This handles the logic of linking an Event to a Family
    public Family addEventToFamily(Long familyId, CalendarEvent event) {
        return familyRepository.findById(familyId).map(family -> {
            family.addEvent(event);
            return familyRepository.save(family);
        }).orElseThrow(() -> new RuntimeException("Family not found with id " + familyId));
    }

    public void deleteFamily(Long id) {
        familyRepository.deleteById(id);
    }

    public void addMemberToFamily(long familyId, Person member){
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new RuntimeException("Family not found"));

        family.addMember(member);
        familyRepository.save(family);
    }
}
