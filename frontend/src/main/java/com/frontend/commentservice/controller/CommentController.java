package com.frontend.commentservice.controller;

import com.frontend.commentservice.dto.CommentDTO;
import com.frontend.commentservice.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService service;

    // PAGE
    @GetMapping
    public String page() {
        return "commentservice/comment-endpoints";
    }

    // GET ALL
    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("comments", service.getAll());
        return "commentservice/comments";
    }

    // GET BY ID
    @GetMapping("/id")
    public String getById(@RequestParam Integer id, Model model) {
        try {
            model.addAttribute("comment", service.getById(id));
        } catch (Exception e) {
            model.addAttribute("comment", null);
        }
        return "commentservice/get-comment";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("comment", new CommentDTO());
        return "commentservice/create-comment";
    }

    // CREATE
    @PostMapping("/create")
    public String create(@ModelAttribute CommentDTO c) {
        service.create(c);
        return "redirect:/comment/all";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/comment/all";
    }

    // GET BY TASK
    @GetMapping("/task")
    public String getByTask(@RequestParam Integer taskId, Model model) {
        model.addAttribute("comments", service.getByTask(taskId));
        model.addAttribute("filterLabel", "Task ID: " + taskId);
        return "commentservice/comments";
    }

    // GET BY USER
    @GetMapping("/user")
    public String getByUser(@RequestParam Integer userId, Model model) {
        model.addAttribute("comments", service.getByUser(userId));
        model.addAttribute("filterLabel", "User ID: " + userId);
        return "commentservice/comments";
    }
}