package com.visnevschi.familyhub.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodDateRequest;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodProfileResponse;
import com.visnevschi.familyhub.document.PeriodProfile;
import com.visnevschi.familyhub.document.PeriodRecord;
import com.visnevschi.familyhub.repository.PeriodProfileRepository;
import com.visnevschi.familyhub.utils.Gender;

class PeriodProfileServiceTest {

    @Test
    void startPeriodSetsAutomaticEndDateFromConfiguredPeriodLength() {
        PersonaService personaService = mock(PersonaService.class);
        PeriodProfileRepository repository = mock(PeriodProfileRepository.class);
        PeriodProfileService service = new PeriodProfileService(personaService, repository);

        Persona persona = mock(Persona.class);
        when(persona.getId()).thenReturn(42L);
        when(persona.getGender()).thenReturn(Gender.FEMALE);
        when(personaService.getForEmail("test@example.com")).thenReturn(persona);
        when(repository.findById(42L)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PeriodProfileResponse response = service.startPeriodForEmail(
                "test@example.com",
                new PeriodDateRequest(LocalDate.of(2026, 8, 14))
        );

        assertNotNull(response.records());
        assertEquals(1, response.records().size());
        assertEquals(LocalDate.of(2026, 8, 14), response.records().get(0).startDate());
        assertEquals(LocalDate.of(2026, 8, 18), response.records().get(0).endDate());
        assertEquals(LocalDate.of(2026, 8, 18), response.lastPeriodEndDate());
    }

    @Test
    void deleteRecordRecalculatesHistoryAndPrediction() {
        PersonaService personaService = mock(PersonaService.class);
        PeriodProfileRepository repository = mock(PeriodProfileRepository.class);
        PeriodProfileService service = new PeriodProfileService(personaService, repository);

        Persona persona = mock(Persona.class);
        when(persona.getId()).thenReturn(42L);
        when(personaService.getForEmail("test@example.com")).thenReturn(persona);

        PeriodProfile profile = new PeriodProfile(42L, null);
        profile.setCycleLengthDays(28);
        profile.setPeriodLengthDays(5);
        profile.setPredictionEnabled(true);
        profile.getPeriodRecords().add(new PeriodRecord("first", LocalDate.of(2026, 6, 1), 5));
        profile.getPeriodRecords().add(new PeriodRecord("wrong", LocalDate.of(2026, 7, 10), 5));
        profile.setLastPeriodStartDate(LocalDate.of(2026, 7, 10));
        profile.setLastPeriodEndDate(LocalDate.of(2026, 7, 14));
        profile.setLearnedCycleLengthDays(39);
        profile.setLearningSamples(1);

        when(repository.findById(42L)).thenReturn(Optional.of(profile));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PeriodProfileResponse response = service.deleteRecordForEmail("test@example.com", "wrong");

        assertEquals(1, response.records().size());
        assertEquals("first", response.records().get(0).id());
        assertEquals(LocalDate.of(2026, 6, 1), response.lastPeriodStartDate());
        assertEquals(LocalDate.of(2026, 6, 5), response.lastPeriodEndDate());
        assertNull(response.learnedCycleLengthDays());
        assertEquals(0, response.learningSamples());
        assertEquals(LocalDate.of(2026, 6, 29), response.nextPredictedStartDate());
    }
}
