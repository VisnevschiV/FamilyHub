package com.visnevschi.familyhub.dbenitity;

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

    @Column(unique = true, nullable = false, length = 8)
    private String joinCode;

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

    public String getJoinCode() { return joinCode; }
    public void setJoinCode(String joinCode) { this.joinCode = joinCode; }
}
