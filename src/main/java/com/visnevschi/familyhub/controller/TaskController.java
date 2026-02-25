package com.visnevschi.familyhub.controller;



import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.dto.TaskList.TaskListCreationRequest;
import com.visnevschi.familyhub.dto.TaskList.TaskListsResponse;
import com.visnevschi.familyhub.service.TaskListService;

import jakarta.validation.Valid;

import com.visnevschi.familyhub.service.FamilyService;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskListService taskListService;

    public TaskController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

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
    
}