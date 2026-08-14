package com.visnevschi.familyhub.service;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodDateRequest;
import com.visnevschi.familyhub.dto.PeriodProfile.PeriodProfileResponse;
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
}
