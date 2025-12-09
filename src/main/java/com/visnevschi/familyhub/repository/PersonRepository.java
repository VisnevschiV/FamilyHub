package com.visnevschi.familyhub.repository;

import com.visnevschi.familyhub.dbenitity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    // That's it!
    // Spring automatically gives you methods like:
    // .save(), .findAll(), .findById(), .delete()
}