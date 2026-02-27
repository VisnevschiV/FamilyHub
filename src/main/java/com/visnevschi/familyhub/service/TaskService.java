package com.visnevschi.familyhub.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.document.Task;
import com.visnevschi.familyhub.document.TaskList;
import com.visnevschi.familyhub.exception.NotFoundException;
import com.visnevschi.familyhub.repository.TaskListRepository;

@Service
public class TaskService {
    private final FamilyService familyService;
    private final TaskListRepository taskListRepository;
    
    public TaskService(TaskListRepository taskListRepository, FamilyService familyService) {
        this.taskListRepository = taskListRepository;
        this.familyService = familyService;
    }

    public void createTask(String userEmail, String listId, String taskName) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        TaskList l = taskListRepository.findByIdAndFamilyId(listId, familyId);
        if (l == null) {
            throw new NotFoundException("Task list not found");
        }
        Task t = new Task(UUID.randomUUID().toString(), taskName, false);
        l.addTask(t);
        taskListRepository.save(l);
    }

    public void deleteTask(String userEmail, String listId, String taskId) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        TaskList l = taskListRepository.findByIdAndFamilyId(listId, familyId);
        if (l == null) {
            throw new NotFoundException("Task list not found");
        }
        l.removeTask(taskId);
        taskListRepository.save(l);
    }

    public void modifyTask(String userEmail, String listId, String taskId, String newName, Boolean completed) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        TaskList l = taskListRepository.findByIdAndFamilyId(listId, familyId);
        if (l == null) {
            throw new NotFoundException("Task list not found");
        }
        Task t = l.getTask(taskId);
        if (t == null) {
            throw new NotFoundException("Task not found");
        }
        if (newName != null) {
            t.setName(newName);
        }
        if (completed != null) {
            t.setCompleted(completed);
        }
        taskListRepository.save(l);
    }
    
}
