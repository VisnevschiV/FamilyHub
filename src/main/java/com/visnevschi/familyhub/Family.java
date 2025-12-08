package com.visnevschi.familyhub;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private Set<Person> members = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "family_id")
    private Set<CalendarEvent> events = new HashSet<>();

    public Family() {}

    public Family(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public Set<Person> getMembers() { return members; }

    public Set<CalendarEvent> getEvents() { return events; }

    public void setName(String name) { this.name = name; }

    public void setMembers(Set<Person> members) { this.members = members; }

    public void setEvents(Set<CalendarEvent> events) { this.events = events; }

    public void addMember(Person member) { members.add(member); }

    public void addEvent(CalendarEvent event) { events.add(event); }
}
