package com.visnevschi.familyhub.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.document.Notification;
import com.visnevschi.familyhub.dto.notifications.NotificationResponse;
import com.visnevschi.familyhub.repository.NotificationRepository;
import com.visnevschi.familyhub.repository.PersonaRepository;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private final NotificationRepository notificationRepository;
    private final PersonaRepository personaRepository;
    private final NotificationEmitterRegistry emitterRegistry;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository,
                               PersonaRepository personaRepository,
                               NotificationEmitterRegistry emitterRegistry,
                               EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.personaRepository = personaRepository;
        this.emitterRegistry = emitterRegistry;
        this.emailService = emailService;
    }

    @Async("notificationExecutor")
    public CompletableFuture<Void> createNotification(Long personaId, String message) {
        try {
            Notification notification = new Notification(personaId, message);
            notificationRepository.save(notification);

            NotificationResponse response = new NotificationResponse(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getCreatedAt(),
                    notification.isRead());
            emitterRegistry.broadcast(personaId, response);

        } catch (Exception e) {
            log.error("Failed to save notification for personaId={}: {}", personaId, e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }

    public Long resolvePersonaId(String email) {
        return personaRepository.findByUserAccountEmail(email)
                .map(persona -> persona.getId())
                .orElseThrow(() -> new IllegalArgumentException("Persona not found for email: " + email));
    }

    public Page<Notification> getNotificationsForUser(String userEmail, Integer page, Integer size) {
        Long personaId = personaRepository.findByUserAccountEmail(userEmail)
                .map(persona -> persona.getId())
                .orElseThrow(() -> new IllegalArgumentException("Persona not found for email: " + userEmail));

        int safePage = page == null || page < 0 ? 0 : page;
        int requestedSize = size == null || size <= 0 ? DEFAULT_PAGE_SIZE : size;
        int safeSize = Math.min(requestedSize, MAX_PAGE_SIZE);

        PageRequest pageable = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByPersonaId(personaId, pageable);
    }

    public void markAsRead(String userEmail, String notificationId) {
        Long personaId = personaRepository.findByUserAccountEmail(userEmail)
            .map(persona -> persona.getId())
            .orElseThrow(() -> new IllegalArgumentException("Persona not found for email: " + userEmail));

        Notification notification = notificationRepository.findByIdAndPersonaId(notificationId, personaId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found for this user"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
