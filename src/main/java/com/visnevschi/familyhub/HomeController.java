package com.visnevschi.familyhub;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 1. This annotation tells Spring: "I handle web requests"
public class HomeController {

    // 2. This maps the root URL (http://localhost:8080/) to this function
    @GetMapping("/") 
    public String home() {
        // The string returned here is what you will see in the browser
        return "Welcome to the FamilyHub Backend!"; 
    }

    // 3. This maps a specific path (http://localhost:8080/check)
    @GetMapping("/check")
    public String checkSystem() {
        return "All systems operational.";
    }
}
