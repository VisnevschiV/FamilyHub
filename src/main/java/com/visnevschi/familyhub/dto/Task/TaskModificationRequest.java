package com.visnevschi.familyhub.dto.Task;

public class TaskModificationRequest {
        String listID;
        String taskID;
        String newName;
        Boolean completed;
    
        public String getListID() {
            return listID;
        }
    
        public String getTaskID() {
            return taskID;
        }
    
        public String getNewName() {
            return newName;
        }
    
        public Boolean getCompleted() {
            return completed;
        }
}
