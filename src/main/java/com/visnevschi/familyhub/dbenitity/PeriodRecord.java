package com.visnevschi.familyhub.dbenitity;

import java.time.LocalDate;

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
@Table(name = "period_events")
public class PeriodRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false)
    private PeriodProfile profile;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    protected PeriodRecord() {
    }

    public PeriodRecord(PeriodProfile profile, LocalDate startDate) {
        this.profile = profile;
        this.startDate = startDate;
    }

    public Long getId() {
        return id;
    }

    public PeriodProfile getProfile() {
        return profile;
    }

    public void setProfile(PeriodProfile profile) {
        this.profile = profile;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
