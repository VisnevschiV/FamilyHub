package com.visnevschi.familyhub.dto.TaskList;

import java.util.List;

import com.visnevschi.familyhub.document.TaskList;

public class TaskListsResponse {
    List<TaskList> taskLists;

    public List<TaskList> getTaskLists() {
        return taskLists;
    }

    public void setTaskLists(List<TaskList> taskLists) {
        this.taskLists = taskLists;
    }
}
