package com.visnevschi.familyhub.dto.Task;

import jakarta.validation.constraints.NotBlank;

public class TaskDeleteRequest {
    
    @NotBlank
    String listId;

    @NotBlank
    String taskId;

    public String getListId() {
        return listId;
    }

    public String getTaskId() {
        return taskId;
    }
}
