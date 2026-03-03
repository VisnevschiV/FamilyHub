package com.visnevschi.familyhub.dbenitity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @OneToMany(mappedBy = "family")
    private final List<Persona> members = new ArrayList<>();

    protected Family() {
    }

    public Family(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Persona> getMembers() {
        return members;
    }

    public void addMember(Persona persona) {
        if (persona == null || members.contains(persona)) {
            return;
        }
        members.add(persona);
        persona.setFamily(this);
    }

    public void removeMember(Persona persona) {
        if (persona == null) {
            return;
        }
        if (members.remove(persona)) {
            persona.setFamily(null);
        }
    }
}
