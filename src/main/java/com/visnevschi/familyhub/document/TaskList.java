package com.visnevschi.familyhub.document;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotNull;

@Document(collection = "task_lists")
public class TaskList {

    @Id
    private String id;

    @Field("family_id")
    @NotNull
    @Indexed
    private Long familyId;

    @Field("name")
    private String name;

    @Field("tasks")
    private List<Task> tasks = new ArrayList<>();

    public TaskList(){}
    
    public TaskList(String name , Long familyId) {
        this.name = name;
        this.familyId = familyId;  
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        if (task == null) {
            return;
        }
        tasks.add(task);
    }
}
