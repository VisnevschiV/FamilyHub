package com.visnevschi.familyhub.document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "period_profiles")
public class PeriodProfile {

    @Id
    private Long id; // personaId

    @Field("family_id")
    @Indexed
    private Long familyId;

    @Field("period_records")
    private List<PeriodRecord> periodRecords = new ArrayList<>();

    @Field("cycle_length_days")
    private Integer cycleLengthDays;

    @Field("period_length_days")
    private Integer periodLengthDays;

    @Field("prediction_enabled")
    private Boolean predictionEnabled;

    @Field("last_period_start_date")
    private LocalDate lastPeriodStartDate;

    @Field("last_period_end_date")
    private LocalDate lastPeriodEndDate;

    @Field("learned_cycle_length_days")
    private Integer learnedCycleLengthDays;

    @Field("learning_samples")
    private Integer learningSamples;

    @Field("next_predicted_start_date")
    private LocalDate nextPredictedStartDate;

    protected PeriodProfile() {
    }

    public PeriodProfile(Long personaId, Long familyId) {
        this.id = personaId;
        this.familyId = familyId;
    }

    public Long getId() {
        return id;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setFamilyId(Long familyId) {
        this.familyId = familyId;
    }

    public List<PeriodRecord> getPeriodRecords() {
        return periodRecords;
    }

    public void setPeriodRecords(List<PeriodRecord> periodRecords) {
        this.periodRecords = periodRecords;
    }

    public Integer getCycleLengthDays() {
        return cycleLengthDays;
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
