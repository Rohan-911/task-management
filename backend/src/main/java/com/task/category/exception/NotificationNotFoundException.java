package com.task.category.exception;


public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(Long id) {
        super("Notification not found with ID: " + id);
    }
}
