package com.visnevschi.familyhub.service;

import java.security.Security;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visnevschi.familyhub.config.VapidProperties;
import com.visnevschi.familyhub.dbenitity.PushSubscription;
import com.visnevschi.familyhub.repository.PushSubscriptionRepository;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service
public class WebPushService {

    private static final Logger log = LoggerFactory.getLogger(WebPushService.class);
    private static final int MAX_FAILURE_COUNT = 10;

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;
    private final PushService pushService;

    public WebPushService(VapidProperties vapid,
                          PushSubscriptionRepository pushSubscriptionRepository,
                          ObjectMapper objectMapper) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.objectMapper = objectMapper;

        PushService ps = null;
        if (StringUtils.hasText(vapid.getPublicKey()) && StringUtils.hasText(vapid.getPrivateKey())) {
            try {
                if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                    Security.addProvider(new BouncyCastleProvider());
                }
                ps = new PushService(vapid.getPublicKey(), vapid.getPrivateKey(), vapid.getSubject());
                log.info("Web Push service initialized (VAPID subject={})", vapid.getSubject());
            } catch (Exception e) {
                log.error("Failed to initialize Web Push service — push notifications disabled: {}", e.getMessage());
            }
        } else {
            log.warn("VAPID keys not configured (app.vapid.public-key / app.vapid.private-key) — Web Push disabled");
        }
        this.pushService = ps;
    }

    /**
     * Sends a push notification to all active subscriptions for the given persona.
     * Runs on the notificationExecutor thread pool.
     */
    @Async("notificationExecutor")
    public CompletableFuture<Void> sendToPersona(Long personaId, String title, String body, String url) {
        if (pushService == null) {
            return CompletableFuture.completedFuture(null);
        }
        List<PushSubscription> subs = pushSubscriptionRepository.findByPersonaIdAndActiveTrue(personaId);
        for (PushSubscription sub : subs) {
            sendToSubscription(sub, title, body, url);
        }
        return CompletableFuture.completedFuture(null);
    }

    private void sendToSubscription(PushSubscription sub, String title, String body, String url) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                    "title", title,
                    "body", body,
                    "url", url
            ));
            Notification notification = new Notification(sub.getEndpoint(), sub.getP256dh(), sub.getAuth(), payload);
            HttpResponse response = pushService.send(notification);
            int status = response.getStatusLine().getStatusCode();

            if (status == 200 || status == 201) {
                sub.setLastSuccessAt(Instant.now());
                sub.setFailureCount(0);
                pushSubscriptionRepository.save(sub);
                log.debug("Push sent ok: subscriptionId={} personaId={} status={}", sub.getId(), sub.getPersonaId(), status);

            } else if (status == 404 || status == 410) {
                log.info("Push subscription gone: subscriptionId={} personaId={} status={} — deactivating",
                        sub.getId(), sub.getPersonaId(), status);
                sub.setActive(false);
                sub.setLastFailureAt(Instant.now());
                pushSubscriptionRepository.save(sub);

            } else if (status == 429) {
                log.warn("Push rate-limited: subscriptionId={} personaId={} — will retry on next event",
                        sub.getId(), sub.getPersonaId());
                recordFailure(sub);

            } else {
                log.warn("Push failed: subscriptionId={} personaId={} status={}", sub.getId(), sub.getPersonaId(), status);
                recordFailure(sub);
            }

        } catch (Exception e) {
            log.error("Push error: subscriptionId={} personaId={} error={}", sub.getId(), sub.getPersonaId(), e.getMessage());
            recordFailure(sub);
        }
    }

    private void recordFailure(PushSubscription sub) {
        sub.setLastFailureAt(Instant.now());
        sub.setFailureCount(sub.getFailureCount() + 1);
        if (sub.getFailureCount() >= MAX_FAILURE_COUNT) {
            log.warn("Disabling subscription after {} failures: subscriptionId={} personaId={}",
                    sub.getFailureCount(), sub.getId(), sub.getPersonaId());
            sub.setActive(false);
        }
        pushSubscriptionRepository.save(sub);
    }
}
