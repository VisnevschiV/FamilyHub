package com.visnevschi.familyhub.dbenitity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity // This tells Hibernate: "Make a table out of this class"
public class Person {

    @Id // This is the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment (1, 2, 3...)
    private Long id;

    @OneToOne(mappedBy = "person")
    private UserAccount userAccount;

    private String name;
    private String role; // e.g., "Husband", "Wife", "Kid"

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private Set<CalendarEvent> events = new HashSet<>();

    @JoinColumn(name = "family_id")
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JsonBackReference
    private Family family;



    // Standard Constructors
    public Person() {
    }

    public Person(String name, String role) {
        this.name = name;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setFamily(Family family) {
        this.family = family;
        family.getMembers().add(this);
    }

    public Family getFamily() {
        return family;
    }

    public Set<CalendarEvent> getEvents() {
        return events;
    }

    public void addEvent(CalendarEvent event) {
        events.add(event);
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }
}