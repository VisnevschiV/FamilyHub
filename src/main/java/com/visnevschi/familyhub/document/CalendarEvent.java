package com.visnevschi.familyhub.document;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.mongodb.lang.NonNull;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document(collection = "calendar_events")
@CompoundIndexes({
        @CompoundIndex(name = "event_family_time_idx", def = "{'family_id': 1, 'time': 1}")
})
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


    @Field("end_time")
    private Instant endTime;

    private boolean isAllDayEvent;
    
    @NotBlank
    @Field("title")
    private String title;
    
    @Size(max = 500)
    @Field("description")
    private String description;

    private HashSet<Long> participants;

    protected CalendarEvent() {
    }

    public CalendarEvent(String title, String description, Instant time, Instant endTime, Long familyId , Long... participants) {
        this(title, description, time, endTime, false, familyId, participants);
    }

    public CalendarEvent(String title, String description, Instant time, Instant endTime, boolean allDayEvent, Long familyId , Long... participants) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.endTime = endTime;
        this.isAllDayEvent = allDayEvent;
        this.familyId = familyId;
        this.participants = new HashSet<>();
        Collections.addAll(this.participants, participants);
    }

    public CalendarEvent(String title, String description, Instant time, Long familyId, Long... participants) {
        this(title, description, time, null, false, familyId, participants);
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

    public Instant getEndTime() {
        return endTime;
    }

    public boolean isAllDayEvent() {
        return isAllDayEvent;
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

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public void setAllDayEvent(boolean allDayEvent) {
        isAllDayEvent = allDayEvent;
    }
}
