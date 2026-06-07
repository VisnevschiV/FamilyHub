package com.visnevschi.familyhub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.dto.push.CreatePushSubscriptionRequest;
import com.visnevschi.familyhub.dto.push.DeletePushSubscriptionRequest;
import com.visnevschi.familyhub.dto.push.PushSubscriptionStatusResponse;
import com.visnevschi.familyhub.service.PushSubscriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/push/subscriptions")
public class PushSubscriptionController {

    private final PushSubscriptionService pushSubscriptionService;

    public PushSubscriptionController(PushSubscriptionService pushSubscriptionService) {
        this.pushSubscriptionService = pushSubscriptionService;
    }

    /**
     * Registers or updates a push subscription for the authenticated user's device.
     * Body is the raw PushSubscription JSON from the browser:
     * { "endpoint": "https://...", "expirationTime": null, "keys": { "p256dh": "...", "auth": "..." } }
     */
    @PostMapping
    public org.springframework.http.ResponseEntity<Void> subscribe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreatePushSubscriptionRequest request) {
        boolean created = pushSubscriptionService.upsert(jwt.getSubject(), request);
        return created
                ? org.springframework.http.ResponseEntity.status(HttpStatus.CREATED).build()
                : org.springframework.http.ResponseEntity.ok().build();
    }

    /**
     * Deactivates the given endpoint subscription.
     * Idempotent — returns 204 even if the subscription does not exist.
     */
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody DeletePushSubscriptionRequest request) {
        pushSubscriptionService.deactivate(jwt.getSubject(), request.endpoint());
    }

    /**
     * Returns whether the current device (identified by endpoint query param) is actively subscribed.
     * Useful for the settings UI to reconcile subscription state.
     */
    @GetMapping("/me")
    public PushSubscriptionStatusResponse status(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String endpoint) {
        boolean subscribed = pushSubscriptionService.isSubscribed(jwt.getSubject(), endpoint);
        return new PushSubscriptionStatusResponse(subscribed);
    }
}
