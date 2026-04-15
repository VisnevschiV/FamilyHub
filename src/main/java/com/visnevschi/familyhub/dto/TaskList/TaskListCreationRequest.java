package com.visnevschi.familyhub.dto.TaskList;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskListCreationRequest {
    @Size(max = 200)
    @NotBlank
    String name;

    Set<Long> participants;

    public String getName() {
        return name;
    }

    public Set<Long> getParticipants() {
        return participants;
    }
}
