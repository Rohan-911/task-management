package com.task.category.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "Notification") // 👈 match table name exactly
public class Notification {

    public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
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

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NotificationID")  // ✅ exact DB column
    private Long notificationId;

    @NotBlank(message = "Text cannot be empty")
    @Column(name = "Text")  // ✅ exact DB column
    private String text;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UserID")  // ✅ exact DB column
    private Long userId;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters & Setters
}