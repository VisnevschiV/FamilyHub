package com.visnevschi.familyhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.visnevschi.familyhub.dbenitity.PushSubscription;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {

    Optional<PushSubscription> findByEndpoint(String endpoint);

    List<PushSubscription> findByPersonaIdAndActiveTrue(Long personaId);

    boolean existsByPersonaIdAndEndpointAndActiveTrue(Long personaId, String endpoint);
}
