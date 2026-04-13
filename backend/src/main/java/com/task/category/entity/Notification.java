package com.task.category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "Notification") //  match table name exactly
public class Notification {

    public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Id
    
    @Column(name = "NotificationID")  
    private int notificationId;

    @NotBlank(message = "Text cannot be empty")
    @Column(name = "Text")  
    private String text;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UserID")  
    private int userId;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
}