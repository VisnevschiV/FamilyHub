package com.visnevschi.familyhub.controller;


import com.visnevschi.familyhub.dto.Mapper;
import com.visnevschi.familyhub.dto.person.PersonCreateDto;
import com.visnevschi.familyhub.dto.person.PersonGeneralDto;
import com.visnevschi.familyhub.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/person")
public class PersonController {


    private final PersonService personService;
    private final Mapper mapper;

    public PersonController(PersonService personService, Mapper mapper) {
        this.personService = personService;
        this.mapper = mapper;
    }

    @GetMapping("/{id}")
    public PersonGeneralDto getPersonById(@PathVariable Long id) {
        return mapper.toGeneral(personService.get(id));
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable Long id) {
        personService.deleteAccount(personService.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonGeneralDto createPerson(@Valid @RequestBody PersonCreateDto person) {
        return mapper.toGeneral(personService.save(mapper.toEntity(person)));
    }

}
