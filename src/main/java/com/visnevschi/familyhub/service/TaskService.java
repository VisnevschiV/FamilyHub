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
    private final NotificationService notificationService;
    
    public TaskService(TaskListRepository taskListRepository, FamilyService familyService, NotificationService notificationService) {
        this.taskListRepository = taskListRepository;
        this.familyService = familyService;
        this.notificationService = notificationService;
    }

    public Task createTask(String userEmail, String listId, String taskName) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        Long personaId = notificationService.resolvePersonaId(userEmail);
        TaskList l = requireAccessibleTaskList(listId, familyId, personaId);

        Task t = new Task(UUID.randomUUID().toString(), taskName, false);
        l.addTask(t);
        taskListRepository.save(l);
        notificationService.createNotification(familyId, "New task added: " + taskName);
        return t;
    }

    public void deleteTask(String userEmail, String listId, String taskId) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        Long personaId = notificationService.resolvePersonaId(userEmail);
        TaskList l = requireAccessibleTaskList(listId, familyId, personaId);

        l.removeTask(taskId);
        taskListRepository.save(l);
    }

    public Task modifyTask(String userEmail, String listId, String taskId, String newName, Boolean completed) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        Long personaId = notificationService.resolvePersonaId(userEmail);
        TaskList l = requireAccessibleTaskList(listId, familyId, personaId);

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
        return t;
    }

    private TaskList requireAccessibleTaskList(String listId, Long familyId, Long personaId) {
        TaskList taskList = taskListRepository.findVisibleByIdAndFamilyIdAndPersonaId(listId, familyId, personaId);
        if (taskList == null) {
            throw new NotFoundException("Task list not found");
        }
        return taskList;
    }
    
}
