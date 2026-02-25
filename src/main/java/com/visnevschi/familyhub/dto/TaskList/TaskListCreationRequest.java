package com.visnevschi.familyhub.dto.TaskList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskListCreationRequest {
    @Size(max = 200)
    @NotBlank
    String name;

    public String getName() {
        return name;
    }
}
