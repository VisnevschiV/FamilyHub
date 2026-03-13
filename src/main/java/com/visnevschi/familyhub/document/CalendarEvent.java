package com.visnevschi.familyhub.document;

import java.time.Instant;
import java.util.HashSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.lang.NonNull;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document(collection = "calendar_events")
public class CalendarEvent {
    @Id
    private String id;

    @Field("family_id")
    @NotNull
    @Indexed
    private Long familyId;

    @NonNull
    @Field("time")
    private Instant time;
    
    @NotBlank
    @Field("title")
    private String title;
    
    @Size(max = 500)
    @Field("description")
    private String description;

    private HashSet<Long> participants;

    protected CalendarEvent() {
    }

    public CalendarEvent(String title, String description, Instant time, Long familyId , Long... participants) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.familyId = familyId;
        this.participants = new HashSet<>();
        for (Long participant : participants) {
            this.participants.add(participant);
        }
    }

    public String getId() {
        return id;
    }

    public HashSet<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(HashSet<Long> participants) {
        this.participants = participants;
    }

    public void addParticipant(Long participant) {
        participants.add(participant);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Instant getTime() {
        return time;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
