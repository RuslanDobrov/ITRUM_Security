package ru.itrum.springSecurity.task01.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ModeratorService {
    @PreAuthorize("hasRole('ROLE_MODERATOR') and hasRole('ROLE_SUPER_ADMIN')")
    public void doModeratorStuff() {
        System.out.println("Only moderator and admin here");
    }
}
