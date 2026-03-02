package com.visnevschi.familyhub.service;

import org.springframework.stereotype.Service;

import com.visnevschi.familyhub.document.TaskList;
import com.visnevschi.familyhub.dto.TaskList.TaskListCreationRequest;
import com.visnevschi.familyhub.dto.TaskList.TaskListsResponse;
import com.visnevschi.familyhub.exception.NotFoundException;
import com.visnevschi.familyhub.repository.TaskListRepository;

@Service
public class TaskListService {
    private final FamilyService familyService;
    private final TaskListRepository taskListRepository;

    public TaskListService(TaskListRepository taskListRepository, FamilyService familyService) {
        this.taskListRepository = taskListRepository;
        this.familyService = familyService;
    }

    public void createTaskList(TaskListCreationRequest request, String userEmail) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        TaskList taskList = new TaskList(request.getName(), familyId);
        taskListRepository.save(taskList);
    }

    public TaskListsResponse getTasksForUser(String userEmail) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        TaskListsResponse response = new TaskListsResponse();
        response.setTaskLists(taskListRepository.findAllByFamilyId(familyId));
        return response;
    }

    public void deleteTaskList(String listId, String userEmail) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        long deleted = taskListRepository.deleteByIdAndFamilyId(listId, familyId);
        if (deleted == 0) {
            throw new NotFoundException("Task list not found");
        }
    }

    public void modifyTaskListName(String listId, String newName, String userEmail) {
        Long familyId = familyService.getFamilyIdForUser(userEmail);
        TaskList taskList = taskListRepository.findByIdAndFamilyId(listId, familyId);
        if (taskList == null) {
            throw new NotFoundException("Task list not found");
        }
        taskList.setName(newName);
        taskListRepository.save(taskList);
    }
}
