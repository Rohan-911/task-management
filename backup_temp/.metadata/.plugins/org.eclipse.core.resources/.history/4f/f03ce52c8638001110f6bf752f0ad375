package com.task.category.service;

import com.task.category.entity.Notification;
import com.task.category.repository.NotificationRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    
    public Notification createNotification(Notification notification) {

        
        Integer nextId = repo.findMaxId() == null ? 1 : repo.findMaxId() + 1;

        notification.setNotificationId(nextId);

        return repo.save(notification);
    }

    
    public List<Notification> getAllNotifications() {
        return repo.findAll();
    }

   
    public Notification getNotificationById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    
    public Notification updateNotification(Integer id, Notification updated) {

        Notification existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));

        existing.setText(updated.getText());
        existing.setUserId(updated.getUserId());

        return repo.save(existing);
    }

    
    public void deleteNotification(Integer id) {
        repo.deleteById(id);
    }

    
    public List<Notification> getNotificationsByUserId(Integer userId) {
        return repo.findByUserId(userId);
    }

    
    public List<Notification> searchByText(String text) {
        return repo.findByTextContaining(text);
    }

    
    public List<Notification> getLatestNotifications() {
        return repo.findTop5ByOrderByCreatedAtDesc();
    }
}