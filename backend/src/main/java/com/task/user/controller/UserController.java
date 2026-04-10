package com.task.user.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.user.dto.UserResponseDTO;
import com.task.user.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserResponseDTO getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/users/me")
    public UserResponseDTO getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "User Dashboard Access Granted";
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/admin")
    public String adminAccess() {
        return "Admin/Manager Access Granted";
    }

    @GetMapping("/users/search")
    public List<UserResponseDTO> searchUsers(@RequestParam String username) {
        return userService.searchUsers(username);
    }
}