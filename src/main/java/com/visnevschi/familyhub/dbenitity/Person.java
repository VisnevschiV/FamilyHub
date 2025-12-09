package com.visnevschi.familyhub.dbenitity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity // This tells Hibernate: "Make a table out of this class"
public class Person {

    @Id // This is the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment (1, 2, 3...)
    private Long id;

    private String name;
    private String role; // e.g., "Husband", "Wife", "Kid"
    private String email;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private Set<CalendarEvent> events = new HashSet<>();

    @JoinColumn(name = "family_id")
    @ManyToOne
    @JsonBackReference
    private Family family;

    // Standard Constructors
    public Person() {
    }

    public Person(String name, String role, String email) {
        this.name = name;
        this.role = role;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFamily(Family family) {
        this.family = family;
    }

    public Set<CalendarEvent> getEvents() {
        return events;
    }

    public void addEvent(CalendarEvent event) {
        events.add(event);
    }
}