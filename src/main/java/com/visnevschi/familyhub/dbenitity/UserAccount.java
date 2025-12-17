package com.visnevschi.familyhub.dbenitity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

@Entity
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "person_id", nullable = false, unique = true)
    private Person person;


    public Long getId() { return id; }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
}
