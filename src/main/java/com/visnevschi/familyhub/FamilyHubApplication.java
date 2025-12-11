package com.visnevschi.familyhub;

import com.visnevschi.familyhub.dbenitity.CalendarEvent;
import com.visnevschi.familyhub.dbenitity.Family;
import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.repository.CalendarEventRepository;
import com.visnevschi.familyhub.repository.FamilyRepository;
import com.visnevschi.familyhub.repository.PersonRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class FamilyHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyHubApplication.class, args);
    }

    // This method runs automatically after the application starts
//    @Bean
//    public CommandLineRunner demo() {
//
//    }
}
