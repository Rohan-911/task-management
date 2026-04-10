package com.task.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.task.category.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}