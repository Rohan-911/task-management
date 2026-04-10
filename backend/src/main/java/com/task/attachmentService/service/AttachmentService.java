package com.task.attachmentService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.attachmentService.entitiy.Attachment;
import com.task.attachmentService.repository.AttachmentRepository;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository repo;

    public Attachment save(Attachment a) {
        return repo.save(a);
    }
}