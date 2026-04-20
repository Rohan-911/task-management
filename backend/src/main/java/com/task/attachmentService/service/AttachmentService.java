package com.task.attachmentService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.task.attachmentService.entitiy.Attachment;
import com.task.attachmentService.repository.AttachmentRepository;

import java.util.List;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository repo;

    // CREATE
    @jakarta.transaction.Transactional
    public Attachment save(Attachment a) {
        Integer maxId = repo.findMaxId();
        if (maxId == null) {
            a.setAttachmentID(1);
        } else {
            a.setAttachmentID(maxId + 1);
        }
        return repo.save(a);
    }

    // GET ALL
    public List<Attachment> getAll() {
        return repo.findAll();
    }

    // GET PAGINATED
    public org.springframework.data.domain.Page<Attachment> getAllPaginated(org.springframework.data.domain.Pageable pageable) {
        return repo.findAll(pageable);
    }

    // GET BY ID
    public Attachment getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new com.task.exception.AttachmentNotFoundException("Attachment not found with ID: " + id));
    }

    // DELETE
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new com.task.exception.AttachmentNotFoundException("Cannot delete. Attachment not found with ID: " + id);
        }
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
    @Transactional
    public void deleteByTask(Integer taskId) {
        repo.deleteByTask_TaskID(taskId);
    }
}