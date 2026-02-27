package com.visnevschi.familyhub.dto.TaskList;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskListModifyRequest {
    
    @NotBlank
    String id;
    
    @Size(max = 200)
    @NotBlank
    String newName;

    public String getId() {
        return id;
    }

    public String getNewName() {
        return newName;
    }
}
