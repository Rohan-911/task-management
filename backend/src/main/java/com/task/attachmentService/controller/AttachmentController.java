package com.task.attachmentService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task.attachmentService.entitiy.Attachment;
import com.task.attachmentService.service.AttachmentService;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService  service;

    @PostMapping
    public Attachment add(@RequestBody Attachment a) {
        return service.save(a);
    }
}