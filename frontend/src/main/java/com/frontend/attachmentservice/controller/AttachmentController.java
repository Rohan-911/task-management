package com.frontend.attachmentservice.controller;

import com.frontend.attachmentservice.dto.AttachmentDTO;
import com.frontend.attachmentservice.service.AttachmentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

    // LOGIN PAGE
    @GetMapping("/login")
    public String showLogin() {
        return "commentservice/unified-login";
    }

    // MAIN PAGE
    @GetMapping
    public String page(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        return "attachmentservice/attachment-endpoints";
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
            com.frontend.commentservice.dto.PagedResponse<AttachmentDTO> pagedData = service.getAllPaginated(page, size);
            model.addAttribute("attachments", pagedData.getContent());
            model.addAttribute("currentPage", pagedData.getNumber());
            model.addAttribute("totalPages", pagedData.getTotalPages());
            model.addAttribute("totalElements", pagedData.getTotalElements());
            model.addAttribute("size", pagedData.getSize());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("attachments", List.of());
        }
        return "attachmentservice/attachments";
    }

    // GET BY ID
    @GetMapping("/id")
    public String getById(@RequestParam Integer id, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        try {
            model.addAttribute("attachment", service.getById(id));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("attachment", null);
        }
        return "attachmentservice/get-attachment";
    }

    // CREATE FORM
    @GetMapping("/create")
    public String createForm(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        model.addAttribute("attachment", new AttachmentDTO());
        return "attachmentservice/create-attachment";
    }

    // CREATE
    @PostMapping("/create")
    public String create(@ModelAttribute AttachmentDTO a, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        try {
            service.create(a);
            return "redirect:/attachment/all";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("attachment", a);
            return "attachmentservice/create-attachment";
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
        return "redirect:/attachment/all";
    }

    // GET BY TASK
    @GetMapping("/task")
    public String getByTask(@RequestParam Integer taskId, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        try {
            model.addAttribute("attachments", service.getByTask(taskId));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("attachments", List.of());
        }
        model.addAttribute("filterLabel", "Task ID: " + taskId);
        return "attachmentservice/attachments";
    }

    // COUNT BY TASK
    @GetMapping("/count")
    public String count(@RequestParam Integer taskId, Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        try {
            model.addAttribute("count", service.countByTask(taskId));
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("count", 0);
        }
        return "attachmentservice/count";
    }

    // DELETE BY TASK
    @GetMapping("/delete/task")
    public String deleteByTask(@RequestParam Integer taskId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("JWT_TOKEN") == null) {
            return "redirect:/comment/login";
        }
        if (!isAdmin(session)) {
            return "access-denied";
        }
        service.deleteByTask(taskId);
        return "redirect:/attachment/all";
    }

    private boolean isAdmin(HttpSession session) {
        List<String> roles = (List<String>) session.getAttribute("ROLES");
        return roles != null && roles.stream().anyMatch(r -> r.equalsIgnoreCase("admin") || r.equalsIgnoreCase("ROLE_ADMIN"));
    }
}