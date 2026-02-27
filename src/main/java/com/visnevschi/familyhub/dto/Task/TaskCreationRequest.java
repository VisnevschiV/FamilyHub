package com.visnevschi.familyhub.dto.Task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskCreationRequest {
    
    @NotBlank
    String listID;

    @NotBlank
    @Size(max = 200)
    String taskName;

    public String getListID() {
        return listID;
    }

    public String getTaskName() {
        return taskName;
    }
}
