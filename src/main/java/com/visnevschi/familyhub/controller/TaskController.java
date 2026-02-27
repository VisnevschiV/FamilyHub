package com.visnevschi.familyhub.controller;



import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.dto.Task.TaskCreationRequest;
import com.visnevschi.familyhub.dto.Task.TaskDeleteRequest;
import com.visnevschi.familyhub.dto.Task.TaskModificationRequest;


import com.visnevschi.familyhub.dto.TaskList.TaskListCreationRequest;
import com.visnevschi.familyhub.dto.TaskList.TaskListModifyRequest;
import com.visnevschi.familyhub.dto.TaskList.TaskListsResponse;


import com.visnevschi.familyhub.service.TaskListService;
import com.visnevschi.familyhub.service.TaskService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskListService taskListService;
    private final TaskService taskService;

    public TaskController(TaskListService taskListService, TaskService taskService) {
        this.taskListService = taskListService;
        this.taskService = taskService;
    }


    @PostMapping("")
    public void createTask(@AuthenticationPrincipal Jwt jwt,
                                @Valid @RequestBody TaskCreationRequest request
    ) {
        taskService.createTask(jwt.getSubject(), request.getListID(), request.getTaskName());
    }
    
    @DeleteMapping("")
    public void deleteTask(@AuthenticationPrincipal Jwt jwt, @RequestBody TaskDeleteRequest request) {
        taskService.deleteTask(jwt.getSubject(), request.getListID(), request.getTaskID());
    }

    @PatchMapping("")
    public void modifyTask(@AuthenticationPrincipal Jwt jwt, @RequestBody TaskModificationRequest request) {
        taskService.modifyTask(jwt.getSubject(), request.getListID(), request.getTaskID(), request.getNewName(), request.getCompleted());
    }

    //Lists operations
    //TODO: new controller for them?
    //TODO: return the ID of the created list maybe? not to read it again to get it
    @PostMapping("/createList")
    public void  postMethodName(@AuthenticationPrincipal Jwt jwt,
                                   @Valid @RequestBody TaskListCreationRequest request
    ) {
        taskListService.createTaskList(request, jwt.getSubject());
    }

    @GetMapping("/getLists")
    public TaskListsResponse getLists(@AuthenticationPrincipal Jwt jwt) {
        return taskListService.getTasksForUser(jwt.getSubject());
    }

    @DeleteMapping("/lists/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteList(@AuthenticationPrincipal Jwt jwt, @PathVariable String id) {
        taskListService.deleteTaskList(id, jwt.getSubject());
    }
    
    @PatchMapping("/lists")
    public void updateList(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TaskListModifyRequest request) {
        taskListService.modifyTaskListName(request.getId(), request.getNewName(), jwt.getSubject());
    }
}