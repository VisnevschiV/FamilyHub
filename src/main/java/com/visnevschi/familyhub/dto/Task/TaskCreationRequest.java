package com.visnevschi.familyhub.dto.Task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskCreationRequest {
    
    @NotBlank
    String listId;

    @NotBlank
    @Size(max = 200)
    String taskName;

    public String getListId() {
        return listId;
    }

    public String getTaskName() {
        return taskName;
    }
}
