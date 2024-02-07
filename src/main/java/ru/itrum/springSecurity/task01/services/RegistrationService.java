package ru.itrum.springSecurity.task01.services;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itrum.springSecurity.task01.models.Person;
import ru.itrum.springSecurity.task01.repositories.PeopleRepository;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RegistrationService {

    @Value("${max_failed_login_attempts}")
    private int maxFailedLoginAttempts;

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    @Transactional
    public void register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        person.setRole("ROLE_USER");
        peopleRepository.save(person);
    }

    public void updateFailedLoginAttempts(String username) {
        Optional<Person> person = peopleRepository.findByUsername(username);
        if (person.isPresent()) {
            int failedAttempts = person.get().getFailedLoginAttempts() + 1;
            person.get().setFailedLoginAttempts(failedAttempts);
            if (failedAttempts >= maxFailedLoginAttempts) {
                person.get().setAccountLocked(true);
                logger.info("Account with username {} has been locked due to too many failed login attempts.", username);
            }
            peopleRepository.save(person.get());
        }
    }

    public void resetFailedLoginAttempts(String username) {
        Optional<Person> person = peopleRepository.findByUsername(username);
        person.get().setFailedLoginAttempts(0);
        peopleRepository.save(person.get());
    }
}
