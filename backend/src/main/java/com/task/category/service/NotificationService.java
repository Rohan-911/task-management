package com.task.category.service;

import com.task.category.entity.Notification;
import com.task.category.repository.NotificationRepository;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class NotificationService {
	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repo;

    public NotificationService(NotificationRepository repo) {
        this.repo = repo;
    }

    public Notification createNotification(Notification notification) {
    	logger.info("Notification received: {}", notification.getText());

        Notification saved = repo.save(notification);

        return saved;
    }

    public List<Notification> getAllNotifications() {
        return repo.findAll();
    }
    
}