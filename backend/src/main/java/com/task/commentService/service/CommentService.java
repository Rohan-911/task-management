package com.task.commentService.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.commentService.entity.Comment;
import com.task.commentService.repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository repo;
    
    @jakarta.transaction.Transactional
    public Comment save(Comment c) {
        // get max ID from DB
        Integer maxId = repo.findMaxId();
        
        if (maxId == null) {
            c.setCommentID(1);
        } else {
            c.setCommentID(maxId + 1);
        }

        if (c.getCreatedAt() == null) {
            c.setCreatedAt(new Date());
        }

        System.out.println("Saving Comment with ID: " + c.getCommentID() + " for Task: " + c.getTaskID());
        
        return repo.save(c);
    }

    
    // GET BY ID
    public Comment getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new com.task.exception.CommentNotFoundException("Comment not found with ID: " + id));
    }
    
    // GET ALL
    public List<Comment> getAll() {
        return repo.findAll();
    }

    // GET PAGINATED
    public org.springframework.data.domain.Page<Comment> getAllPaginated(org.springframework.data.domain.Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    // DELETE
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new com.task.exception.CommentNotFoundException("Cannot delete. Comment not found with ID: " + id);
        }
        repo.deleteById(id);
    }
    
    // GET BY TASK
    public List<Comment> getByTaskId(Integer taskId) {
        return repo.findByTaskID(taskId);
    }
    
    // GET USER BY ID 
    public List<Comment> getByUser(Integer userId) {
    return repo.findByUserID(userId);
    }
}
