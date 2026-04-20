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
    //GET ALL COMMENT
    @GetMapping 
    public List<Comment> getAll() {
        return service.getAll();
    }

    // GET PAGINATED
    @GetMapping("/list/all")
    public org.springframework.data.domain.Page<Comment> getAllPaginated(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {
        return service.getAllPaginated(org.springframework.data.domain.PageRequest.of(page, size));
    }

    // GET BY ID
    @GetMapping("/item/{id:[0-9]+}")
    public org.springframework.http.ResponseEntity<Comment> getById(@PathVariable Integer id) {
        Comment c = service.getById(id);
        if (c == null) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
        return org.springframework.http.ResponseEntity.ok(c);
    }

    // DELETE
    @DeleteMapping("/item/{id:[0-9]+}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "Comment deleted"; // FIXED (match test)
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
