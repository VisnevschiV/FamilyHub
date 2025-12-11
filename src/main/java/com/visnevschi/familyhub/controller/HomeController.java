package com.visnevschi.familyhub.controller;

import com.visnevschi.familyhub.dbenitity.CalendarEvent;
import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.repository.FamilyRepository;
import com.visnevschi.familyhub.repository.PersonRepository;
import com.visnevschi.familyhub.service.FamilyService;
import com.visnevschi.familyhub.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HomeController {

    // Inject the Repository
    @Autowired
    private PersonService personService;

    @Autowired
    private FamilyService familyService;

    @GetMapping("/")
    public String home() {
        return "Welcome to FamilyHub!";
    }

    // GET /members -> Returns a JSON list of all family members in the DB
    @GetMapping("/members")
    public List<Person> getAllMembers() {
        return personService.findAll();
    }

    // POST /members -> Adds a new member (received as JSON)
    @PostMapping("/members")
    public Person addMember(@RequestBody Person newMember) {
        return personService.save(newMember);
    }

    @GetMapping("/families")
    public List<Family> getAllFamilies() {

        return familyService.findAllFamilies();
    }

    @PostMapping("/families/{id}/events")
    public Family addEventToFamily(@PathVariable Long id, @RequestBody CalendarEvent event) {
        return familyService.addEventToFamily(id, event);
    }

    @PostMapping("/families/{familyId}/members/{personId}")
    public void addMemberToFamily(@PathVariable Long familyId, @PathVariable Long personId) {
        // Now this works because we updated the Service above!
        familyService.addMemberToFamily(familyId, personId);
    }
}