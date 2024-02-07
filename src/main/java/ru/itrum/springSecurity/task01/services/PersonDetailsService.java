package ru.itrum.springSecurity.task01.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.itrum.springSecurity.task01.models.Person;
import ru.itrum.springSecurity.task01.repositories.PeopleRepository;
import ru.itrum.springSecurity.task01.security.PersonDetails;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonDetailsService implements UserDetailsService {
    private final PeopleRepository peopleRepository;
    private static final Logger logger = LoggerFactory.getLogger(PersonDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByUsername(username);
        if (person.isEmpty()) {
            logger.error("User with login: " + username + ", is not found");
            throw new UsernameNotFoundException("User not found!");
        }
        return new PersonDetails(person.get());
    }
}