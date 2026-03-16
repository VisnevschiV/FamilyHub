package com.visnevschi.familyhub.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class NotificationEmitterRegistry {

    private static final Logger log = LoggerFactory.getLogger(NotificationEmitterRegistry.class);

    // personaId -> list of open emitters (one per browser tab / client)
    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * Creates and registers a new SseEmitter for the given persona.
     * Automatically cleans itself up when the client disconnects.
     */
    public SseEmitter register(Long personaId) {
        SseEmitter emitter = new SseEmitter(0L); // 0 = no server-side timeout

        emitters.computeIfAbsent(personaId, id -> new CopyOnWriteArrayList<>()).add(emitter);

        // Remove emitter from registry when the connection closes for any reason
        Runnable cleanup = () -> remove(personaId, emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> {
            log.debug("SSE error for personaId={}: {}", personaId, e.getMessage());
            remove(personaId, emitter);
        });

        log.debug("SSE client connected: personaId={}, total connections={}", personaId,
                emitters.getOrDefault(personaId, new CopyOnWriteArrayList<>()).size());
        return emitter;
    }

    /**
     * Pushes a payload to all active SSE connections for a persona.
     * Dead emitters are removed automatically.
     */
    public void broadcast(Long personaId, @org.springframework.lang.NonNull Object payload) {
        List<SseEmitter> targets = emitters.getOrDefault(personaId, new CopyOnWriteArrayList<>());
        for (SseEmitter emitter : targets) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(payload));
            } catch (IOException e) {
                log.debug("Dead emitter removed for personaId={}", personaId);
                remove(personaId, emitter);
            }
        }
    }

    /**
     * Sends a heartbeat ping to all connected clients every 25 seconds.
     * This keeps connections alive through proxies and load balancers.
     */
    @Scheduled(fixedDelay = 25_000)
    public void heartbeat() {
        emitters.forEach((personaId, list) -> {
            for (SseEmitter emitter : list) {
                try {
                    emitter.send(SseEmitter.event().name("ping").data(""));
                } catch (IOException e) {
                    remove(personaId, emitter);
                }
            }
        });
    }

    private void remove(Long personaId, SseEmitter emitter) {
        List<SseEmitter> list = emitters.get(personaId);
        if (list != null) {
            list.remove(emitter);
        }
    }
}
