package ru.itrum.springSecurity.task01.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itrum.springSecurity.task01.services.AdminService;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.v1.prefix}")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/admin")
    public String adminPage() {
        adminService.doAdminStuff();
        return "admin";
    }
}
