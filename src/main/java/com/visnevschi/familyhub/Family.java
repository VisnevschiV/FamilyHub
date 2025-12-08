package com.visnevschi.familyhub;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private Set<Person> members;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "family_id")
    private Set<CalendarEvent> events;
}
