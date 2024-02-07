package ru.itrum.springSecurity.task01.util;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.itrum.springSecurity.task01.models.Person;
import ru.itrum.springSecurity.task01.services.PersonDetailsService;

@RequiredArgsConstructor
@Component
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;
    private static final Logger logger = LoggerFactory.getLogger(PersonValidator.class);

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        try {
            personDetailsService.loadUserByUsername(person.getUsername());
        } catch (UsernameNotFoundException ignored) {
            return; //person found
        }
        errors.rejectValue("username", "", "Person is already exists");
        logger.error("User with login: " + person.getUsername() + ", is already exists");
    }
}