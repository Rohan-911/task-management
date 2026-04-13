package com.task.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.task.category.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    
	List<Notification> findByUserId(Integer userId);

    List<Notification> findByTextContaining(String text);

    List<Notification> findTop5ByOrderByCreatedAtDesc();
    @Query("SELECT MAX(n.notificationId) FROM Notification n")
    Integer findMaxId();
}