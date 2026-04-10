package com.task.attachmentService.entitiy;

import org.springframework.scheduling.config.Task;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Attachment {

    @Id
    private Integer attachmentID;

    private String fileName;
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "taskID")
    private Task task;
}