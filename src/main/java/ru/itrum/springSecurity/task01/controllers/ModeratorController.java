package ru.itrum.springSecurity.task01.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itrum.springSecurity.task01.services.ModeratorService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/moderator")
public class ModeratorController {
    private final ModeratorService moderatorService;

    @GetMapping()
    public String moderatorPage() {
        moderatorService.doModeratorStuff();
        return "moderator";
    }
}
