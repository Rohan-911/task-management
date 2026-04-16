package com.task.attachmentService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.attachmentService.entitiy.Attachment;
import com.task.attachmentService.repository.AttachmentRepository;

import java.util.List;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository repo;

    // CREATE
    public Attachment save(Attachment a) {
        return repo.save(a);
    }

    // GET ALL
    public List<Attachment> getAll() {
        return repo.findAll();
    }

    // GET BY ID
    public Attachment getById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    // DELETE
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    // GET BY TASK ID (FIXED)
    public List<Attachment> getByTaskId(Integer taskId) {
        return repo.findByTask_TaskID(taskId);
    }

    // NEW: COUNT
    public int countByTask(Integer taskId) {
        return repo.countByTask_TaskID(taskId);
    }

    // NEW: DELETE BY TASK
    public void deleteByTask(Integer taskId) {
        repo.deleteByTask_TaskID(taskId);
    }
}