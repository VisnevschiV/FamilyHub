package com.visnevschi.familyhub.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.visnevschi.familyhub.service.DailyUpdatesService;


@RestController
public class HomeController {

    private final DailyUpdatesService dailyUpdatesService;

    public HomeController(DailyUpdatesService dailyUpdatesService) {
        this.dailyUpdatesService = dailyUpdatesService;
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to FamilyHub!";
    }

    @PostMapping("/admin/run-daily-cleanup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void triggerDailyCleanup() {
        dailyUpdatesService.deleteCompletedTasks();
    }
}