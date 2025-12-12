package com.visnevschi.familyhub.controller;


import org.springframework.web.bind.annotation.*;


@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to FamilyHub!";
    }
}