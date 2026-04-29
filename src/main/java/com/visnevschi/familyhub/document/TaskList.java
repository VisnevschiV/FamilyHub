package com.visnevschi.familyhub.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;

@Document(collection = "task_lists")
public class TaskList {

    @Id
    private String id;

    @Field("family_id")
    @NotNull
    @Indexed
    private Long familyId;

    @Field("name")
    private String name;

    @Field("tasks")
    private List<Task> tasks = new ArrayList<>();

    @Field("participants")
    private HashSet<Long> participants = new HashSet<>();

    public TaskList(){}
    
    public TaskList(String name , Long familyId , Long... participants) {
        this.name = name;
        this.familyId = familyId;
        this.participants = new HashSet<>();
        if (participants != null) {
            Collections.addAll(this.participants, participants);
        }
    }

    public HashSet<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(HashSet<Long> participants) {
        this.participants = participants == null ? new HashSet<>() : participants;
    }

    public boolean isUserParticipant(Long userId) {

        return participants == null || participants.isEmpty() || participants.contains(userId);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Long getFamilyId() {
        return familyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        if (task.getId() == null || task.getId().isBlank()) {
            task.setId(UUID.randomUUID().toString());
        }
        tasks.add(task);
    }

    public void removeTask(String taskId) {
        tasks.removeIf(t -> Objects.equals(t.getId(), taskId));
    }

    public Task getTask(String taskId) {
        return tasks.stream().filter(t -> Objects.equals(t.getId(), taskId)).findFirst().orElse(null);
    }
}
