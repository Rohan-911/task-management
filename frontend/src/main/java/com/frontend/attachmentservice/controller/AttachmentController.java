package com.frontend.attachmentservice.controller;

import com.frontend.attachmentservice.dto.AttachmentDTO;
import com.frontend.attachmentservice.service.AttachmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService service;

    // MAIN PAGE
    @GetMapping
    public String page() {
        return "attachmentservice/attachment-endpoints";
    }

    // GET ALL
    @GetMapping("/all")
    public String getAll(Model model) {
        model.addAttribute("attachments", service.getAll());
        return "attachmentservice/attachments";
    }

    // GET BY ID
    @GetMapping("/id")
    public String getById(@RequestParam Integer id, Model model) {
        try {
            model.addAttribute("attachment", service.getById(id));
        } catch (Exception e) {
            model.addAttribute("attachment", null);
        }
        return "attachmentservice/get-attachment";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("attachment", new AttachmentDTO());
        return "attachmentservice/create-attachment";
    }

    // CREATE
    @PostMapping("/create")
    public String create(@ModelAttribute AttachmentDTO a) {
        service.create(a);
        return "redirect:/attachment/all";
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/attachment/all";
    }

    // GET BY TASK
    @GetMapping("/task")
    public String getByTask(@RequestParam Integer taskId, Model model) {
        model.addAttribute("attachments", service.getByTask(taskId));
        model.addAttribute("filterLabel", "Task ID: " + taskId);
        return "attachmentservice/attachments";
    }

    // COUNT BY TASK
    @GetMapping("/count")
    public String count(@RequestParam Integer taskId, Model model) {
        model.addAttribute("count", service.countByTask(taskId));
        return "attachmentservice/count";
    }

    // DELETE BY TASK
    @GetMapping("/delete/task")
    public String deleteByTask(@RequestParam Integer taskId) {
        service.deleteByTask(taskId);
        return "redirect:/attachment/all";
    }
}