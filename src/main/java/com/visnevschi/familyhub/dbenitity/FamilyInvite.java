package com.visnevschi.familyhub.dbenitity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "family_invite")
public class FamilyInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    protected FamilyInvite() {
    }

    public FamilyInvite(String code, Instant expiresAt, Family family) {
        this.code = code;
        this.expiresAt = expiresAt;
        this.family = family;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Family getFamily() {
        return family;
    }
}
