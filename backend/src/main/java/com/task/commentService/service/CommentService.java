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
    
    // CREATE
    public Comment save(Comment c) {
        c.setCreatedAt(new Date());
        return repo.save(c);
    }
    
    // GET BY ID
    public Comment getById(Integer id) {
        return repo.findById(id).orElse(null);
    }
    
    // GET ALL
    public List<Comment> getAll() {
    return repo.findAll();
    }
    
    // DELETE
    public void delete(Integer id) {
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
