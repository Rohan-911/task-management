package com.task.category.controller;

import com.task.category.entity.Notification;
import com.task.category.service.NotificationService;

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

    
    @PostMapping("/create")
    public Notification create(@Valid @RequestBody Notification notification) {
        return service.createNotification(notification);
    }

   
    @GetMapping("/all")
    public List<Notification> getAll() {
        return service.getAllNotifications();
    }

    
    @GetMapping("/id/{id}")
    public Notification getById(@PathVariable Integer id) {
        return service.getNotificationById(id);
    }

    
    @PutMapping("/update/{id}")
    public Notification update(@PathVariable Integer id,
                               @Valid @RequestBody Notification notification) {
        return service.updateNotification(id, notification);
    }

  
    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteNotification(id);
        return "Notification deleted successfully";
    }

    
    @GetMapping("/user/{userId}")
    public List<Notification> getByUser(@PathVariable Integer userId) {
        return service.getNotificationsByUserId(userId);
    }

    
    @GetMapping("/search")
    public List<Notification> search(@RequestParam String text) {
        return service.searchByText(text);
    }

    
    @GetMapping("/latest")
    public List<Notification> getLatest() {
        return service.getLatestNotifications();
    }
}