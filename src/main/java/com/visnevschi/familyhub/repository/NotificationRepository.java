package com.visnevschi.familyhub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.visnevschi.familyhub.document.Notification;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    Page<Notification> findByPersonaId(Long personaId, Pageable pageable);
    java.util.Optional<Notification> findByIdAndPersonaId(String id, Long personaId);
}
