package com.visnevschi.familyhub.service;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visnevschi.familyhub.dbenitity.PushSubscription;
import com.visnevschi.familyhub.dto.push.CreatePushSubscriptionRequest;
import com.visnevschi.familyhub.repository.PersonaRepository;
import com.visnevschi.familyhub.repository.PushSubscriptionRepository;

@Service
public class PushSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(PushSubscriptionService.class);

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final PersonaRepository personaRepository;

    public PushSubscriptionService(PushSubscriptionRepository pushSubscriptionRepository,
                                   PersonaRepository personaRepository) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.personaRepository = personaRepository;
    }

    /**
     * Upserts a push subscription for the authenticated user.
     * Returns true if a new row was created, false if an existing row was updated.
     */
    @Transactional
    public boolean upsert(String userEmail, CreatePushSubscriptionRequest request) {
        Long personaId = resolvePersonaId(userEmail);
        Optional<PushSubscription> existing = pushSubscriptionRepository.findByEndpoint(request.endpoint());

        if (existing.isPresent()) {
            PushSubscription sub = existing.get();
            sub.setPersonaId(personaId);
            sub.setP256dh(request.keys().p256dh());
            sub.setAuth(request.keys().auth());
            sub.setExpirationTime(request.expirationTime());
            sub.setActive(true);
            sub.setFailureCount(0);
            if (request.userAgent() != null) sub.setUserAgent(request.userAgent());
            if (request.platform() != null) sub.setPlatform(request.platform());
            pushSubscriptionRepository.save(sub);
            log.info("Push subscription updated: subscriptionId={} personaId={}", sub.getId(), personaId);
            return false;
        }

        PushSubscription sub = new PushSubscription();
        sub.setPersonaId(personaId);
        sub.setEndpoint(request.endpoint());
        sub.setP256dh(request.keys().p256dh());
        sub.setAuth(request.keys().auth());
        sub.setExpirationTime(request.expirationTime());
        sub.setUserAgent(request.userAgent());
        sub.setPlatform(request.platform());
        pushSubscriptionRepository.save(sub);
        log.info("Push subscription created: subscriptionId={} personaId={}", sub.getId(), personaId);
        return true;
    }

    /**
     * Deactivates (or no-ops if missing) the subscription with the given endpoint for the authenticated user.
     */
    @Transactional
    public void deactivate(String userEmail, String endpoint) {
        pushSubscriptionRepository.findByEndpoint(endpoint).ifPresent(sub -> {
            sub.setActive(false);
            sub.setLastFailureAt(Instant.now());
            pushSubscriptionRepository.save(sub);
            log.info("Push subscription deactivated: subscriptionId={}", sub.getId());
        });
    }

    /**
     * Returns whether the current user has an active subscription for the given endpoint.
     */
    public boolean isSubscribed(String userEmail, String endpoint) {
        Long personaId = resolvePersonaId(userEmail);
        return pushSubscriptionRepository.existsByPersonaIdAndEndpointAndActiveTrue(personaId, endpoint);
    }

    private Long resolvePersonaId(String email) {
        return personaRepository.findByUserAccountEmail(email)
                .map(p -> p.getId())
                .orElseThrow(() -> new IllegalArgumentException("Persona not found for email: " + email));
    }
}
