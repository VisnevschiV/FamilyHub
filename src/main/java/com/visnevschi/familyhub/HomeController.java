package com.visnevschi.familyhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HomeController {

    // Inject the Repository
    @Autowired
    private PersonRepository repository;

    @GetMapping("/")
    public String home() {
        return "Welcome to FamilyHub!";
    }

    // GET /members -> Returns a JSON list of all family members in the DB
    @GetMapping("/members")
    public List<Person> getAllMembers() {
        return repository.findAll();
    }

    // POST /members -> Adds a new member (received as JSON)
    @PostMapping("/members")
    public Person addMember(@RequestBody Person newMember) {
        return repository.save(newMember);
    }
}