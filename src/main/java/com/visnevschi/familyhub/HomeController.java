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
    private FamilyMemberRepository repository;

    @GetMapping("/")
    public String home() {
        return "Welcome to FamilyHub!";
    }

    // GET /members -> Returns a JSON list of all family members in the DB
    @GetMapping("/members")
    public List<FamilyMember> getAllMembers() {
        return repository.findAll();
    }

    // POST /members -> Adds a new member (received as JSON)
    @PostMapping("/members")
    public FamilyMember addMember(@RequestBody FamilyMember newMember) {
        return repository.save(newMember);
    }
}