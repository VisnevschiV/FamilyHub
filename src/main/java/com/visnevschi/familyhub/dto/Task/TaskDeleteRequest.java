package com.visnevschi.familyhub.dto.Task;

import jakarta.validation.constraints.NotBlank;

public class TaskDeleteRequest {
    
    @NotBlank
    String listID;

    @NotBlank
    String taskID;

    public String getListID() {
        return listID;
    }

    public String getTaskID() {
        return taskID;
    }
}
