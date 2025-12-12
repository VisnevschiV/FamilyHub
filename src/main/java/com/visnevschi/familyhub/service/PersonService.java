package com.visnevschi.familyhub.service;


import com.visnevschi.familyhub.dbenitity.Person;
import com.visnevschi.familyhub.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class PersonService {

    @Autowired
    private PersonRepository repository;


    public List<Person> findAll() {
        return repository.findAll();
    }

    public Person get(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Person not found"));
    }
    public Person save(Person person) {
        return repository.save(person);
    }

    public void deleteAccount(Person person) {
        repository.deleteById(person.getId());
    }

}
