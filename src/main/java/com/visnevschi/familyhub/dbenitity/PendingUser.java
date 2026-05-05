package com.visnevschi.familyhub.dbenitity;

import java.time.LocalDate;

import com.visnevschi.familyhub.utils.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import com.visnevschi.familyhub.utils.GeneratedCode;

import jakarta.persistence.EnumType;
import java.time.Instant;

@Entity
public class PendingUser implements GeneratedCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String code;

    private Instant codeExpiration;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;


    protected PendingUser() {}


    public PendingUser(String email, String password, String name, LocalDate birthday, Gender gender) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
    }


    @Override
    public void setCode(String code) {
        this.code = code;
    }
    @Override
    public void setExpiresAt(Instant codeExpiration) {
        this.codeExpiration = codeExpiration;
    }
    @Override
    public String getCode() {
        return code;
    }
    @Override
    public Instant getExpiresAt() {
        return codeExpiration;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public Gender getGender() {
        return gender;
    }
}
