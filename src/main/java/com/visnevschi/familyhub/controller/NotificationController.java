package com.visnevschi.familyhub.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.visnevschi.familyhub.document.Notification;
import com.visnevschi.familyhub.dto.notifications.NotificationPageResponse;
import com.visnevschi.familyhub.dto.notifications.NotificationResponse;
import com.visnevschi.familyhub.service.NotificationEmitterRegistry;
import com.visnevschi.familyhub.service.NotificationService;


@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final NotificationEmitterRegistry emitterRegistry;

    public NotificationController(NotificationService notificationService,
                                   NotificationEmitterRegistry emitterRegistry) {
        this.notificationService = notificationService;
        this.emitterRegistry = emitterRegistry;
    }

    @GetMapping("")
    public NotificationPageResponse getNotifications(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Page<Notification> notificationsPage = notificationService.getNotificationsForUser(jwt.getSubject(), page, size);
        java.util.List<NotificationResponse> items = notificationsPage.getContent().stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getCreatedAt(),
                        notification.isRead()))
                .toList();

        return new NotificationPageResponse(
                items,
                notificationsPage.getNumber(),
                notificationsPage.getSize(),
                notificationsPage.getTotalElements(),
                notificationsPage.getTotalPages(),
                notificationsPage.hasNext());
    }

    @PatchMapping("/{notificationId}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markAsRead(@AuthenticationPrincipal Jwt jwt, @PathVariable String notificationId) {
        notificationService.markAsRead(jwt.getSubject(), notificationId);
    }

    /**
     * Opens a persistent SSE stream for the authenticated user.
     * The server will push a "notification" event whenever a new notification is created for this user.
     * A "ping" event is sent every 25 s to keep the connection alive through proxies.
     *
     * Frontend usage:
     *   const es = new EventSource('/notifications/stream', { withCredentials: true });
     *   es.addEventListener('notification', e => console.log(JSON.parse(e.data)));
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@AuthenticationPrincipal Jwt jwt) {
        Long personaId = notificationService.resolvePersonaId(jwt.getSubject());
        return emitterRegistry.register(personaId);
    }
}
