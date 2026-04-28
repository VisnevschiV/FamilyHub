package com.visnevschi.familyhub.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visnevschi.familyhub.dbenitity.PeriodProfile;
import com.visnevschi.familyhub.dbenitity.PeriodRecord;
import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.dto.PeriodProfile.CreatePeriodProfileRequest;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodDateRequest;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodMonthResponse;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodProfileResponse;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodRecordResponse;
import com.visnevschi.familyhub.dto.PeriodProfile.RecordPeriodEventRequest;
import com.visnevschi.familyhub.dto.PeriodProfile.UpdatePeriodProfileRequest;
import com.visnevschi.familyhub.exception.NotFoundException;
import com.visnevschi.familyhub.repository.PeriodProfileRepository;
import com.visnevschi.familyhub.repository.PeriodRecordRepository;
import com.visnevschi.familyhub.utils.Gender;
import com.visnevschi.familyhub.utils.PeriodEventType;

@Service
public class PeriodProfileService {

    private static final int DEFAULT_CYCLE_LENGTH_DAYS = 28;
    private static final int DEFAULT_PERIOD_LENGTH_DAYS = 5;
    private static final int LEARNING_MIN_CYCLE_DAYS = 15;
    private static final int LEARNING_MAX_CYCLE_DAYS = 60;

    private final PersonaService personaService;
    private final PeriodProfileRepository periodProfileRepository;
    private final PeriodRecordRepository periodRecordRepository;

    public PeriodProfileService(PersonaService personaService,
                                PeriodProfileRepository periodProfileRepository,
                                PeriodRecordRepository periodRecordRepository) {
        this.personaService = personaService;
        this.periodProfileRepository = periodProfileRepository;
        this.periodRecordRepository = periodRecordRepository;
    }

    @SuppressWarnings("null")
    @Transactional
    public PeriodProfileResponse createForEmail(String email, CreatePeriodProfileRequest request) {
        Persona persona = personaService.getForEmail(email);
        validatePersonaEligibility(persona);
        Long personaId = Objects.requireNonNull(persona.getId(), "Persona id is required");

        if (periodProfileRepository.existsById(personaId)) {
            throw new IllegalStateException("Period profile already exists for this persona");
        }

        PeriodProfile profile = new PeriodProfile(persona);
        setDefaults(profile);
        applyCreateValues(profile, request);
        normalizeDefaults(profile);
        recalculatePrediction(profile);

        return toResponse(Objects.requireNonNull(periodProfileRepository.save(profile)));
    }

    public PeriodProfileResponse getForEmail(String email) {
        Persona persona = personaService.getForEmail(email);
        Long personaId = Objects.requireNonNull(persona.getId(), "Persona id is required");
        PeriodProfile profile = periodProfileRepository.findById(personaId)
                .orElseThrow(() -> new NotFoundException("Period profile not found"));
        normalizeDefaults(profile);
        recalculatePrediction(profile);
        return toResponse(profile);
    }

    public Optional<PeriodProfileResponse> getForEmailIfExists(String email) {
        Persona persona = personaService.getForEmail(email);
        Long personaId = Objects.requireNonNull(persona.getId(), "Persona id is required");
        return periodProfileRepository.findById(personaId)
                .map(profile -> {
                    normalizeDefaults(profile);
                    recalculatePrediction(profile);
                    return toResponse(profile);
                });
    }

    @SuppressWarnings("null")
    @Transactional
    public PeriodProfileResponse updateForEmail(String email, UpdatePeriodProfileRequest request) {
        Persona persona = personaService.getForEmail(email);
        Long personaId = Objects.requireNonNull(persona.getId(), "Persona id is required");
        PeriodProfile profile = periodProfileRepository.findById(personaId)
                .orElseThrow(() -> new NotFoundException("Period profile not found"));

        if (request.cycleLengthDays() != null) {
            profile.setCycleLengthDays(request.cycleLengthDays());
        }
        if (request.periodLengthDays() != null) {
            profile.setPeriodLengthDays(request.periodLengthDays());
        }
        if (request.predictionEnabled() != null) {
            profile.setPredictionEnabled(request.predictionEnabled());
        }
        if (request.lastPeriodStartDate() != null) {
            profile.setLastPeriodStartDate(request.lastPeriodStartDate());
        }

        normalizeDefaults(profile);
        recalculatePrediction(profile);

        return toResponse(periodProfileRepository.save(profile));
    }

    @SuppressWarnings("null")
    @Transactional
    public PeriodProfileResponse recordEventForEmail(String email, RecordPeriodEventRequest request) {
        if (request.eventType() == PeriodEventType.STARTED) {
            return startPeriodForEmail(email, new PeriodDateRequest(request.date()));
        }
        if (request.eventType() == PeriodEventType.ENDED) {
            return stopPeriodForEmail(email, new PeriodDateRequest(request.date()));
        }
        throw new IllegalArgumentException("Unsupported period event type");
    }

    @SuppressWarnings("null")
    @Transactional
    public PeriodProfileResponse startPeriodForEmail(String email, PeriodDateRequest request) {
        Persona persona = personaService.getForEmail(email);
        validatePersonaEligibility(persona);
        Long personaId = Objects.requireNonNull(persona.getId(), "Persona id is required");

        PeriodProfile profile = periodProfileRepository.findById(personaId)
                .orElseGet(() -> {
                    PeriodProfile created = new PeriodProfile(persona);
                    setDefaults(created);
                    return Objects.requireNonNull(periodProfileRepository.save(created));
                });

        normalizeDefaults(profile);
        recordPeriodStart(profile, request.date());
        learnCycleLengthFromEvents(profile);
        syncLatestPeriodDates(profile);
        recalculatePrediction(profile);

        return toResponse(Objects.requireNonNull(periodProfileRepository.save(profile)));
    }

    @SuppressWarnings("null")
    @Transactional
    public PeriodProfileResponse stopPeriodForEmail(String email, PeriodDateRequest request) {
        Persona persona = personaService.getForEmail(email);
        validatePersonaEligibility(persona);
        Long personaId = Objects.requireNonNull(persona.getId(), "Persona id is required");

        PeriodProfile profile = periodProfileRepository.findById(personaId)
                .orElseThrow(() -> new NotFoundException("Period profile not found"));

        normalizeDefaults(profile);
        recordPeriodEnd(profile, request.date());
        learnCycleLengthFromEvents(profile);
        syncLatestPeriodDates(profile);
        recalculatePrediction(profile);

        return toResponse(Objects.requireNonNull(periodProfileRepository.save(profile)));
    }

    public PeriodMonthResponse getMonthForEmail(String email, int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }

        Persona persona = personaService.getForEmail(email);
        Long personaId = Objects.requireNonNull(persona.getId(), "Persona id is required");
        PeriodProfile profile = periodProfileRepository.findById(personaId).orElse(null);
        if (profile == null) {
            return new PeriodMonthResponse(year, month, List.of(), null);
        }

        normalizeDefaults(profile);
        syncLatestPeriodDates(profile);
        recalculatePrediction(profile);

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        List<PeriodRecord> monthRecords = periodRecordRepository.findMonthRecordsByProfileId(profile.getId(), monthStart, monthEnd);
        List<PeriodRecordResponse> records = monthRecords.stream()
            .filter(record -> {
                if (record.getEndDate() != null) {
                return true;
                }

                // Open periods are treated as lasting the configured/default period length.
                LocalDate expectedEnd = record.getStartDate()
                    .plusDays(Math.max(1, profile.getPeriodLengthDays()) - 1L);
                return !expectedEnd.isBefore(monthStart);
            })
                .map(record -> new PeriodRecordResponse(record.getId(), record.getStartDate(), record.getEndDate(), false))
                .toList();

        PeriodRecordResponse prediction = null;
        if (records.isEmpty()) {
            LocalDate predictedStart = findPredictedStartForMonth(profile, monthStart, monthEnd);
            if (predictedStart != null) {
                LocalDate predictedEnd = predictedStart.plusDays(Math.max(1, profile.getPeriodLengthDays()) - 1L);
                prediction = new PeriodRecordResponse(null, predictedStart, predictedEnd, true);
            }
        }

        return new PeriodMonthResponse(year, month, records, prediction);
    }

    @Transactional
    public void deleteForEmail(String email) {
        Persona persona = personaService.getForEmail(email);
        Long personaId = Objects.requireNonNull(persona.getId(), "Persona id is required");
        if (!periodProfileRepository.existsById(personaId)) {
            throw new NotFoundException("Period profile not found");
        }
        periodProfileRepository.deleteById(personaId);
    }

    private void validatePersonaEligibility(Persona persona) {
        if (persona.getGender() != Gender.FEMALE) {
            throw new IllegalArgumentException("Period tracking can be enabled only for FEMALE personas");
        }
    }

    private void applyCreateValues(PeriodProfile profile, CreatePeriodProfileRequest request) {
        if (request == null) {
            return;
        }
        if (request.cycleLengthDays() != null) {
            profile.setCycleLengthDays(request.cycleLengthDays());
        }
        if (request.periodLengthDays() != null) {
            profile.setPeriodLengthDays(request.periodLengthDays());
        }
        if (request.predictionEnabled() != null) {
            profile.setPredictionEnabled(request.predictionEnabled());
        }
        if (request.lastPeriodStartDate() != null) {
            profile.setLastPeriodStartDate(request.lastPeriodStartDate());
        }
    }

    private void setDefaults(PeriodProfile profile) {
        profile.setCycleLengthDays(DEFAULT_CYCLE_LENGTH_DAYS);
        profile.setPeriodLengthDays(DEFAULT_PERIOD_LENGTH_DAYS);
        profile.setPredictionEnabled(Boolean.TRUE);
        profile.setLearningSamples(0);
    }

    private void normalizeDefaults(PeriodProfile profile) {
        if (profile.getCycleLengthDays() == null) {
            profile.setCycleLengthDays(DEFAULT_CYCLE_LENGTH_DAYS);
        }
        if (profile.getPeriodLengthDays() == null) {
            profile.setPeriodLengthDays(DEFAULT_PERIOD_LENGTH_DAYS);
        }
        if (profile.getPredictionEnabled() == null) {
            profile.setPredictionEnabled(Boolean.TRUE);
        }
        if (profile.getLearningSamples() == null) {
            profile.setLearningSamples(0);
        }
    }

    private void recordPeriodStart(PeriodProfile profile, LocalDate startDate) {
        if (periodRecordRepository.existsByProfileIdAndStartDate(profile.getId(), startDate)) {
            throw new IllegalStateException("A period with this start date already exists");
        }

        if (periodRecordRepository.findFirstByProfileIdAndEndDateIsNullOrderByStartDateDescIdDesc(profile.getId()).isPresent()) {
            throw new IllegalStateException("There is already an active period without an end date");
        }

        PeriodRecord periodRecord = new PeriodRecord(profile, startDate);
        profile.addPeriodRecord(periodRecord);
        periodRecordRepository.save(periodRecord);
    }

    private void recordPeriodEnd(PeriodProfile profile, LocalDate endDate) {
        PeriodRecord activePeriod = periodRecordRepository
                .findFirstByProfileIdAndEndDateIsNullOrderByStartDateDescIdDesc(profile.getId())
                .orElseThrow(() -> new IllegalStateException("There is no active period to end"));

        if (endDate.isBefore(activePeriod.getStartDate())) {
            throw new IllegalArgumentException("Period end date cannot be before the period start date");
        }

        activePeriod.setEndDate(endDate);
        periodRecordRepository.save(activePeriod);
    }

    private void learnCycleLengthFromEvents(PeriodProfile profile) {
        List<PeriodRecord> periodsDesc = periodRecordRepository
                .findTop6ByProfileIdOrderByStartDateDescIdDesc(profile.getId());

        if (periodsDesc.size() < 2) {
            profile.setLearnedCycleLengthDays(null);
            profile.setLearningSamples(0);
            return;
        }

        List<LocalDate> startsAsc = new ArrayList<>();
        for (PeriodRecord periodRecord : periodsDesc) {
            startsAsc.add(periodRecord.getStartDate());
        }
        Collections.sort(startsAsc);

        int total = 0;
        int samples = 0;
        for (int i = 1; i < startsAsc.size(); i++) {
            long days = ChronoUnit.DAYS.between(startsAsc.get(i - 1), startsAsc.get(i));
            if (days >= LEARNING_MIN_CYCLE_DAYS && days <= LEARNING_MAX_CYCLE_DAYS) {
                total += (int) days;
                samples++;
            }
        }

        profile.setLearningSamples(samples);
        if (samples > 0) {
            int learned = Math.round((float) total / samples);
            profile.setLearnedCycleLengthDays(learned);
        } else {
            profile.setLearnedCycleLengthDays(null);
        }
    }

    private void syncLatestPeriodDates(PeriodProfile profile) {
        periodRecordRepository.findFirstByProfileIdOrderByStartDateDescIdDesc(profile.getId())
            .map(PeriodRecord::getStartDate)
            .ifPresent(profile::setLastPeriodStartDate);

        periodRecordRepository.findFirstByProfileIdAndEndDateIsNotNullOrderByEndDateDescIdDesc(profile.getId())
            .map(PeriodRecord::getEndDate)
            .ifPresent(profile::setLastPeriodEndDate);
    }

    private void recalculatePrediction(PeriodProfile profile) {
        if (!Boolean.TRUE.equals(profile.getPredictionEnabled()) || profile.getLastPeriodStartDate() == null) {
            profile.setNextPredictedStartDate(null);
            return;
        }

        int effectiveCycleLength = resolveEffectiveCycleLength(profile);
        profile.setNextPredictedStartDate(profile.getLastPeriodStartDate().plusDays(effectiveCycleLength));
    }

    private LocalDate findPredictedStartForMonth(PeriodProfile profile, LocalDate monthStart, LocalDate monthEnd) {
        if (!Boolean.TRUE.equals(profile.getPredictionEnabled()) || profile.getLastPeriodStartDate() == null) {
            return null;
        }

        int cycleLength = resolveEffectiveCycleLength(profile);
        if (cycleLength <= 0) {
            return null;
        }

        LocalDate cursor = profile.getLastPeriodStartDate();

        while (cursor.isBefore(monthStart)) {
            cursor = cursor.plusDays(cycleLength);
        }

        while (cursor.isAfter(monthEnd)) {
            cursor = cursor.minusDays(cycleLength);
        }

        if (cursor.isBefore(monthStart) || cursor.isAfter(monthEnd)) {
            return null;
        }

        return cursor;
    }

    private int resolveEffectiveCycleLength(PeriodProfile profile) {
        if (profile.getLearnedCycleLengthDays() != null) {
            return profile.getLearnedCycleLengthDays();
        }
        if (profile.getCycleLengthDays() != null) {
            return profile.getCycleLengthDays();
        }
        return DEFAULT_CYCLE_LENGTH_DAYS;
    }

    private PeriodProfileResponse toResponse(PeriodProfile profile) {
        return new PeriodProfileResponse(
                profile.getId(),
                profile.getCycleLengthDays(),
                resolveEffectiveCycleLength(profile),
                profile.getPeriodLengthDays(),
                profile.getPredictionEnabled(),
                profile.getLastPeriodStartDate(),
                profile.getLastPeriodEndDate(),
                profile.getLearnedCycleLengthDays(),
                profile.getLearningSamples(),
                profile.getNextPredictedStartDate()
        );
    }
}
