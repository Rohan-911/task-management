package com.task.commentService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @GetMapping("/task/{id}")
    public List<Comment> get(@PathVariable Integer id) {
        return service.getByTask(id);
    }
    //get all comment 
    @GetMapping
    public List<Comment> getAll() {
        return service.getAll();
    }
    
    //get comment by user
    @GetMapping("/user/{userId}")
    public List<Comment> getByUser(@PathVariable Integer userId) {
        return service.getByUser(userId);
    }
    
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Comment deleted";
    }
    
    //get comment by id
    @GetMapping("/{id}")
    public Comment getById(@PathVariable Integer id) {
        return service.getById(id);
    }

}