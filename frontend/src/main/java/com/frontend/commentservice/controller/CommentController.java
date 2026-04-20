package com.frontend.commentservice.controller;

import com.frontend.commentservice.dto.CommentDTO;
import com.frontend.commentservice.service.CommentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    // LOGIN PAGE
    @GetMapping("/login")
    public String showLogin() {
        return "commentservice/unified-login";
    }

    // PAGE
    @GetMapping
    public String page(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        return "commentservice/comment-endpoints";
    }

    // GET ALL (PAGINATED)
    @GetMapping("/all")
    public String getAll(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        try {
            com.frontend.commentservice.dto.PagedResponse<CommentDTO> pagedData = service.getAllPaginated(page, size);
            model.addAttribute("comments", pagedData.getContent());
            model.addAttribute("currentPage", pagedData.getNumber());
            model.addAttribute("totalPages", pagedData.getTotalPages());
            model.addAttribute("totalElements", pagedData.getTotalElements());
            model.addAttribute("size", pagedData.getSize());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("comments", List.of());
        }
        return "commentservice/comments";
    }

    // GET BY ID
    @GetMapping("/id")
    public String getById(@RequestParam Integer id, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        try {
            model.addAttribute("comment", service.getById(id));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("comment", null);
        }
        return "commentservice/get-comment";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String createForm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        model.addAttribute("comment", new CommentDTO());
        return "commentservice/create-comment";
    }

    // CREATE
    @PostMapping("/create")
    public String create(@ModelAttribute CommentDTO c, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        try {
            service.create(c);
            return "redirect:/comment/all";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("comment", c);
            return "commentservice/create-comment";
        }
    }

    // DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        if (!isAdmin(session)) {
            return "access-denied";
        }
        service.delete(id);
        return "redirect:/comment/all";
    }

    // GET BY TASK
    @GetMapping("/task")
    public String getByTask(@RequestParam Integer taskId, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        model.addAttribute("comments", service.getByTask(taskId));
        model.addAttribute("filterLabel", "Task ID: " + taskId);
        return "commentservice/comments";
    }

    // GET BY USER
    @GetMapping("/user")
    public String getByUser(@RequestParam Integer userId, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        model.addAttribute("comments", service.getByUser(userId));
        model.addAttribute("filterLabel", "User ID: " + userId);
        return "commentservice/comments";
    }

    private boolean isAdmin(HttpSession session) {
        List<String> roles = (List<String>) session.getAttribute("ROLES");
        return roles != null && roles.stream().anyMatch(r -> r.equalsIgnoreCase("admin") || r.equalsIgnoreCase("ROLE_ADMIN"));
    }
}