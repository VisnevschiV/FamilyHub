package com.visnevschi.familyhub.service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.dbenitity.Persona;
import com.visnevschi.familyhub.document.TaskList;
import com.visnevschi.familyhub.dto.TaskList.TaskListCreationRequest;
import com.visnevschi.familyhub.dto.TaskList.TaskListsResponse;
import com.visnevschi.familyhub.exception.NotFoundException;
import com.visnevschi.familyhub.repository.TaskListRepository;

@Service
public class TaskListService {
    private final FamilyService familyService;
    private final TaskListRepository taskListRepository;
    private final NotificationService notificationService;

    public TaskListService(TaskListRepository taskListRepository, FamilyService familyService , NotificationService notificationService) {
        this.taskListRepository = taskListRepository;
        this.familyService = familyService;
        this.notificationService = notificationService;
    }

    public TaskList createTaskList(TaskListCreationRequest request, String userEmail) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        Long personaId = notificationService.resolvePersonaId(userEmail);
        TaskList taskList = new TaskList(request.getName(), familyId);
        taskList.setParticipants(resolveAndValidateParticipants(request.getParticipants(), userEmail, personaId));
        TaskList savedTaskList = taskListRepository.save(taskList);
        notificationService.createNotification(familyId, "New task list created: " + request.getName());
        return savedTaskList;
    }

    public TaskListsResponse getTasksForUser(String userEmail) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        Long personaId = notificationService.resolvePersonaId(userEmail);
        TaskListsResponse response = new TaskListsResponse();
        response.setTaskLists(taskListRepository.findVisibleByFamilyIdAndPersonaId(familyId, personaId));
        return response;
    }

    public void deleteTaskList(String listId, String userEmail) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        Long personaId = notificationService.resolvePersonaId(userEmail);
        TaskList taskList = requireAccessibleTaskList(listId, familyId, personaId);
        taskListRepository.delete(Objects.requireNonNull(taskList));
        notificationService.createNotification(familyId, "Task list deleted");
    }

    public TaskList modifyTaskListName(String listId, String newName, Set<Long> participants, String userEmail) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        Long personaId = notificationService.resolvePersonaId(userEmail);
        TaskList taskList = requireAccessibleTaskList(listId, familyId, personaId);

        taskList.setName(newName);
        if (participants != null) {
            taskList.setParticipants(resolveAndValidateParticipants(participants, userEmail, personaId));
        }
        TaskList savedTaskList = taskListRepository.save(taskList);
        notificationService.createNotification(familyId, "Task list renamed to: " + newName);
        return savedTaskList;
    }

    private TaskList requireAccessibleTaskList(String listId, Long familyId, Long personaId) {
        TaskList taskList = taskListRepository.findVisibleByIdAndFamilyIdAndPersonaId(listId, familyId, personaId);
        if (taskList == null) {
            throw new NotFoundException("Task list not found");
        }
        return taskList;
    }

    private HashSet<Long> resolveAndValidateParticipants(Set<Long> participantIds, String userEmail, Long personaId) {
        if (participantIds == null || participantIds.isEmpty()) {
            return new HashSet<>();
        }

        if (participantIds.contains(null)) {
            throw new IllegalArgumentException("Participants cannot contain null IDs");
        }

        Set<Long> familyMemberIds = familyService.getFamilyMembersForUser(userEmail)
                .stream()
                .map(Persona::getId)
                .collect(Collectors.toSet());

        if (!familyMemberIds.containsAll(participantIds)) {
            throw new IllegalArgumentException("All participants must belong to your family");
        }

        if (!participantIds.contains(personaId)) {
            throw new IllegalArgumentException("You must be included in participants");
        }

        return new HashSet<>(participantIds);
    }
}
