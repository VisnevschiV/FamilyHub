package com.visnevschi.familyhub.dto.TaskList;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskListModifyRequest {
    
    @NotBlank
    String id;
    
    @Size(max = 200)
    @NotBlank
    String newName;

    Set<Long> participants;
    Boolean completed;

    public String getId() {
        return id;
    }

    public String getNewName() {
        return newName;
    }

    public Set<Long> getParticipants() {
        return participants;
    }

    public Boolean getCompleted() {
        return completed;
    }
}
