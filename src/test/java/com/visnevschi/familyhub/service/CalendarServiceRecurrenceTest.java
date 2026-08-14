package com.visnevschi.familyhub.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.dto.Calendar.RecurrenceFrequency;
import com.visnevschi.familyhub.dto.Calendar.RecurrenceRuleRequest;
import com.visnevschi.familyhub.repository.CalendarEventRepository;

class CalendarServiceRecurrenceTest {

    @Test
    void allowsYearlyRecurrenceWithMonthDayAndIntervalTwelve() throws Exception {
        FamilyService familyService = mock(FamilyService.class);
        CalendarEventRepository repository = mock(CalendarEventRepository.class);
        NotificationService notificationService = mock(NotificationService.class);
        PersonaService personaService = mock(PersonaService.class);

        when(familyService.getFamilyIdForUser(Mockito.anyString())).thenReturn(1L);
        when(repository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        Persona persona = mock(Persona.class);
        when(persona.getId()).thenReturn(1L);
        when(personaService.getForEmail(Mockito.anyString())).thenReturn(persona);

        CalendarService service = new CalendarService(familyService, repository, notificationService, personaService);

        RecurrenceRuleRequest recurrence = new RecurrenceRuleRequest();
        setField(recurrence, "frequency", RecurrenceFrequency.YEARLY);
        setField(recurrence, "interval", 12);
        setField(recurrence, "byMonthDay", 15);

        assertDoesNotThrow(() -> service.createEvent(
                "test@example.com",
                "Birthday",
                "Annual birthday",
                Instant.parse("2024-01-15T10:00:00Z"),
                Instant.parse("2024-01-15T11:00:00Z"),
                false,
                recurrence,
                Set.of()));
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
