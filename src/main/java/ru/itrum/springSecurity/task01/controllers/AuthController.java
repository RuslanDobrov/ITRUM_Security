package ru.itrum.springSecurity.task01.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itrum.springSecurity.task01.dto.AuthenticationDTO;
import ru.itrum.springSecurity.task01.dto.PersonDTO;
import ru.itrum.springSecurity.task01.models.Person;
import ru.itrum.springSecurity.task01.security.JWTUtil;
import ru.itrum.springSecurity.task01.security.PersonDetails;
import ru.itrum.springSecurity.task01.services.RegistrationService;
import ru.itrum.springSecurity.task01.util.PersonValidator;
import javax.validation.Valid;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/public")
public class AuthController {

    private final PersonValidator personValidator;
    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/registration")
    public Map<String, String> performRegistration(@RequestBody @Valid PersonDTO personDTO, BindingResult bindingResult) {
        Person person = convertToPerson(personDTO);
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return Map.of("message", "Error!");
        }
        registrationService.register(person);
        String token = jwtUtil.generateToken(person.getUsername());
        logger.info("User with login: " + person.getUsername() + ", has been registered with token: " + token);
        return Map.of("jwt-token", token);
    }

    @GetMapping("/showUserInfo")
    @ResponseBody
    public String showUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        logger.info("User with login: " + personDetails.getUsername() + ", called the endpoint: showUserInfo");
        return personDetails.getUsername();
    }

    public Person convertToPerson(PersonDTO personDTO) {
        return this.modelMapper.map(personDTO, Person.class);
    }

    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());
        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            registrationService.updateFailedLoginAttempts(authenticationDTO.getUsername());
            return Map.of("message", "Incorrect credentials!");
        }

        registrationService.resetFailedLoginAttempts(authenticationDTO.getUsername());
        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return Map.of("jwt-token", token);
    }
}
