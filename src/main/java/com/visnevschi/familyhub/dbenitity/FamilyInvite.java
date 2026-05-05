package com.visnevschi.familyhub.dbenitity;

import java.time.Instant;

import com.visnevschi.familyhub.utils.GeneratedCode;

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
public class FamilyInvite implements GeneratedCode {

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

    public FamilyInvite() {
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
    public void setFamily(Family family) {
        this.family = family;
    }
    public Family getFamily() {
        return family;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
