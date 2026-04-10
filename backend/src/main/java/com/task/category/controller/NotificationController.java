package com.task.category.controller;

import com.task.category.entity.Notification;
import com.task.category.service.NotificationService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // 🔐 Only AUTHENTICATED USERS
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public Notification create(@Valid @RequestBody Notification notification) {
        return service.createNotification(notification);
    }

    @GetMapping
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }
}