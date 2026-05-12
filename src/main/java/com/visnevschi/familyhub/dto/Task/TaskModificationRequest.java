package com.visnevschi.familyhub.dto.Task;

public class TaskModificationRequest {
        String listId;
        String taskId;
        String newName;
        Boolean completed;
    
        public String getListId() {
            return listId;
        }
    
        public String getTaskId() {
            return taskId;
        }
    
        public String getNewName() {
            return newName;
        }
    
        public Boolean getCompleted() {
            return completed;
        }
}
