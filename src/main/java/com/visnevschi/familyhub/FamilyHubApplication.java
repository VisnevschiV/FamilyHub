package com.visnevschi.familyhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FamilyHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(FamilyHubApplication.class, args);
    }

    // This method runs automatically after the application starts
    @Bean
    public CommandLineRunner demo(PersonRepository memberRepository, CalendarEventRepository eventRepository) {
        return (args) -> {
            // Create a couple of users
            memberRepository.save(new Person("John Doe", "Father", "john@family.com"));
            memberRepository.save(new Person("Jane Doe", "Mother", "jane@family.com"));
            memberRepository.save(new Person("Baby Doe", "Kid", "baby@family.com"));

            System.out.println("Data seeding completed! ");
        };
    }
}
