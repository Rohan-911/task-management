package com.task.attachmentService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.task.attachmentService.entitiy.Attachment;
import com.task.attachmentService.service.AttachmentService;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService service;

    // CREATE
    @PostMapping
    public Attachment add(@RequestBody Attachment a) {
        return service.save(a);
    }

    // GET ALL
    @GetMapping
    public List<Attachment> getAll() {
        return service.getAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public Attachment getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Attachment deleted successfully";
    }

    // GET BY TASK
    @GetMapping("/task/{taskId}")
    public List<Attachment> getByTask(@PathVariable Integer taskId) {
        return service.getByTaskId(taskId);
    }

    // NEW: COUNT
    @GetMapping("/count/{taskId}")
    public int countByTask(@PathVariable Integer taskId) {
        return service.countByTask(taskId);
    }

    // NEW: DELETE BY TASK
    @DeleteMapping("/task/{taskId}")
    public String deleteByTask(@PathVariable Integer taskId) {
        service.deleteByTask(taskId);
        return "All attachments deleted for task " + taskId;
    }
}