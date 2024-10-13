package com.alirezanoroozi.walletisc.Service;

import com.alirezanoroozi.walletisc.Entity.Person;
import com.alirezanoroozi.walletisc.Repository.RepositoryPerson;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ServicePerson {
    @Autowired
    private RepositoryPerson personRepository;



    public void savePerson(Person person) {
        personRepository.save(person);
    }


    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person getPersonById(Long id) {
        Optional<Person> personOptional = personRepository.findById(id);
        return personOptional.orElse(null);
    }

    public void deletePersonById(Long id) {
        try {
            personRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            // می‌توانید یک استثنای سفارشی پرتاب کنید یا لاگ کنید
            throw new EntityNotFoundException("شخص با شناسه " + id + " پیدا نشد.");
        }
    }

    public Person updatePerson(Person person, Long id) {
        // یافتن شخص با شناسه مشخص
        Person findPerson = personRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Person not found with id: " + id));

        // به‌روزرسانی مقادیر
        findPerson.setFirstName(person.getFirstName());
        findPerson.setLastname(person.getLastname());
        findPerson.setNationalId(person.getNationalId());
        findPerson.setDateOfBirth(person.getDateOfBirth());
        findPerson.setGender(person.getGender());
        findPerson.setMilitaryStatus(person.getMilitaryStatus());
        findPerson.setMobileNumber(person.getMobileNumber());
        findPerson.setEmail(person.getEmail());
        findPerson.setUserStatus(person.getUserStatus());

        // ذخیره و بازگشت شخص به‌روزرسانی شده
        return personRepository.save(findPerson);
    }
    private final RestTemplate restTemplate;

    @Autowired
    public ServicePerson(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    public Person fetchUser() {
        ResponseEntity<Person> response = restTemplate.exchange(
                "http://localhost:8080/api/persons/1",
                HttpMethod.GET,
                null,
                Person.class
        );
        return response.getBody();
    }





}

