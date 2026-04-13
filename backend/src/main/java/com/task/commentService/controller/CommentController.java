package com.task.commentService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.task.commentService.entity.Comment;
import com.task.commentService.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService service;

    @PostMapping
    public Comment add(@RequestBody Comment c) {
        return service.save(c);
    }
    //GET ALL COMMENT
    @GetMapping 
    public List<Comment> getAll() {
    return service.getAll();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public Comment getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Comment deleted successfully";
    }
    
    //GET BY TASK
    @GetMapping("/task/{taskId}")
    public List<Comment> getByTask(@PathVariable Integer taskId) {
    return service.getByTaskId(taskId);
    }
    
    //GET BY USER
    @GetMapping("/user/{userId}")
    public List<Comment> getByUser(@PathVariable Integer userId) {
    return service.getByUser(userId);
    }
}
