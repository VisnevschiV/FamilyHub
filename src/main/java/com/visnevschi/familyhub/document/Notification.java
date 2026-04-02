package com.visnevschi.familyhub.document;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "notifications")
@CompoundIndexes({
    @CompoundIndex(name = "notification_persona_created_idx", def = "{'persona_id': 1, 'created_at': -1}"),
    @CompoundIndex(name = "notification_persona_read_created_idx", def = "{'persona_id': 1, 'read': 1, 'created_at': -1}")
})
public class Notification {

    @Id
    private String id;

    @Field("persona_id")
    private Long personaId;

    @Field("message")
    private String message;

    @Field("created_at")
    private Instant createdAt;

    @Field("read")
    private boolean read;

    public Notification() {

        
    }

    public Notification(Long personaId, String message) {
        this.personaId = personaId;
        this.message = message;
        this.createdAt = Instant.now();
        this.read = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
