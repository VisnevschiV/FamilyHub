package com.visnevschi.familyhub.dbenitity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "period_profiles")
public class PeriodProfile {

    @Id
    @Column(name = "persona_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @OneToMany(mappedBy = "profile")
    @OrderBy("startDate ASC, id ASC")
    private List<PeriodRecord> periodRecords = new ArrayList<>();

    @Column(name = "cycle_length_days", nullable = false)
    private Integer cycleLengthDays = 28;

    @Column(name = "period_length_days", nullable = false)
    private Integer periodLengthDays = 5;

    @Column(name = "prediction_enabled", nullable = false)
    private Boolean predictionEnabled = Boolean.TRUE;

    @Column(name = "last_period_start_date")
    private LocalDate lastPeriodStartDate;

    @Column(name = "last_period_end_date")
    private LocalDate lastPeriodEndDate;

    @Column(name = "learned_cycle_length_days")
    private Integer learnedCycleLengthDays;

    @Column(name = "learning_samples", nullable = false)
    private Integer learningSamples = 0;

    @Column(name = "next_predicted_start_date")
    private LocalDate nextPredictedStartDate;

    protected PeriodProfile() {
    }

    public PeriodProfile(Persona persona) {
        this.persona = persona;
    }

    public Long getId() {
        return id;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Integer getCycleLengthDays() {
        return cycleLengthDays;
    }

    public List<PeriodRecord> getPeriodRecords() {
        return periodRecords;
    }

    public void setPeriodRecords(List<PeriodRecord> periodRecords) {
        this.periodRecords = periodRecords;
    }

    public void addPeriodRecord(PeriodRecord periodRecord) {
        if (periodRecord == null) {
            return;
        }
        periodRecord.setProfile(this);
        periodRecords.add(periodRecord);
    }

    public void setCycleLengthDays(Integer cycleLengthDays) {
        this.cycleLengthDays = cycleLengthDays;
    }

    public Integer getPeriodLengthDays() {
        return periodLengthDays;
    }

    public void setPeriodLengthDays(Integer periodLengthDays) {
        this.periodLengthDays = periodLengthDays;
    }

    public Boolean getPredictionEnabled() {
        return predictionEnabled;
    }

    public void setPredictionEnabled(Boolean predictionEnabled) {
        this.predictionEnabled = predictionEnabled;
    }

    public LocalDate getLastPeriodStartDate() {
        return lastPeriodStartDate;
    }

    public void setLastPeriodStartDate(LocalDate lastPeriodStartDate) {
        this.lastPeriodStartDate = lastPeriodStartDate;
    }

    public LocalDate getLastPeriodEndDate() {
        return lastPeriodEndDate;
    }

    public void setLastPeriodEndDate(LocalDate lastPeriodEndDate) {
        this.lastPeriodEndDate = lastPeriodEndDate;
    }

    public Integer getLearnedCycleLengthDays() {
        return learnedCycleLengthDays;
    }

    public void setLearnedCycleLengthDays(Integer learnedCycleLengthDays) {
        this.learnedCycleLengthDays = learnedCycleLengthDays;
    }

    public Integer getLearningSamples() {
        return learningSamples;
    }

    public void setLearningSamples(Integer learningSamples) {
        this.learningSamples = learningSamples;
    }

    public LocalDate getNextPredictedStartDate() {
        return nextPredictedStartDate;
    }

    public void setNextPredictedStartDate(LocalDate nextPredictedStartDate) {
        this.nextPredictedStartDate = nextPredictedStartDate;
    }
}
