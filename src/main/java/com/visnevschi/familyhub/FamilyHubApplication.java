package com.visnevschi.familyhub;

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
    @Bean
    public CommandLineRunner demo(PersonRepository memberRepository,
                                  CalendarEventRepository eventRepository,
                                  FamilyRepository familyRepository) {
        return (args) -> {
            // 1. Create a Family
            Family myFamily = new Family();
            myFamily.setName("The Simpsons");

            // 2. Create a Person
            Person homer = new Person("Homer Simpson", "Father", "homer@simpson.com");

            // Link Person to Family (Bi-directional relationship needs both sides if cascading)
            homer.setFamily(myFamily);
            myFamily.addMember(homer);

            // 3. Create 2 Events for the Person (Homer)
            CalendarEvent workEvent = new CalendarEvent("Work at Plant", "Sector 7G", LocalDateTime.now().plusDays(1));
            CalendarEvent moeEvent = new CalendarEvent("Moe's Tavern", "Hangout with guys", LocalDateTime.now().plusDays(1).plusHours(8));

            homer.getEvents().add(workEvent);
            homer.getEvents().add(moeEvent);

            // 4. Create 1 Event for the Family
            CalendarEvent familyDinner = new CalendarEvent("Family Dinner", "Dinner at Krusty Burger", LocalDateTime.now().plusDays(2));

            myFamily.addEvent(familyDinner);

            // 5. Save everything
            // Because Family has CascadeType.ALL for 'members' and 'events',
            // saving the family will save the person, and the person's events, and the family's events.
            familyRepository.save(myFamily);

            System.out.println("Data seeding completed! Created family 'The Simpsons' with members and events.");
        };
    }
}
