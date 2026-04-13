package com.task.commentService.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.commentService.entity.Comment;
import com.task.commentService.repository.CommentRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository repo;

    public Comment save(Comment c) {
        c.setCreatedAt(LocalDateTime.now());
        return repo.save(c);
    }

    public List<Comment> getByTask(Integer taskId) {
        return repo.findByTask_TaskID(taskId);
    }
    
    public List<Comment> getAll() {
    return repo.findAll();
    }
    
}
