package com.frontend.userservice.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.frontend.userservice.dto.UserRequestDTO;
import com.frontend.userservice.dto.UserResponseDTO;
import com.frontend.userservice.service.FrontendUserService;

import jakarta.validation.Valid;

@Controller
public class ViewController {

    private final FrontendUserService userService;

    public ViewController(FrontendUserService userService) {
        this.userService = userService;
    }

    private String extractErrorMessage(Exception e) {
        if (e instanceof org.springframework.web.client.HttpClientErrorException) {
            org.springframework.web.client.HttpClientErrorException clientEx = (org.springframework.web.client.HttpClientErrorException) e;
            try {
                com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(clientEx.getResponseBodyAsString());
                if (root.has("message")) return root.get("message").asText();
                if (root.has("error")) return root.get("error").asText();
            } catch (Exception ignored) {}
            return clientEx.getStatusText();
        }
        return e.getMessage();
    }

    @GetMapping("/login")
    public String home() {
        return "userservice/login";
    }

    @PostMapping("/logout")
    public String logout(jakarta.servlet.http.HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "userservice/dashboard";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "userservice/admin-dashboard";
    }

    @GetMapping("/user-dashboard")
    public String userDashboard() {
        return "userservice/user-dashboard";
    }

    // ================= USERS =================

    @GetMapping("/api/users")
    public String usersPage(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String success) {

        int size = 10;

        try {
            Page<UserResponseDTO> userPage = userService.getAllUsers(page, size);
            model.addAttribute("users", userPage.getContent());
            model.addAttribute("totalPages", userPage.getTotalPages());
            model.addAttribute("allRoles", userService.getAllRoles());
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
            model.addAttribute("users", List.of());
            model.addAttribute("totalPages", 0);
            model.addAttribute("allRoles", List.of());
        }

        model.addAttribute("currentPage", page);

        if (success != null) {
            model.addAttribute("success", success);
        }

        return "userservice/users";
    }

    // ================= CREATE =================

    @GetMapping("/api/users/new")
    public String createUserPage(Model model) {
        model.addAttribute("userRequestDTO", new UserRequestDTO());
        return "userservice/create-user";
    }

    @PostMapping("/api/users")
    public String createUser(@Valid UserRequestDTO dto,
                             BindingResult result,
                             Model model) {

        model.addAttribute("userRequestDTO", dto);

        if (result.hasErrors()) {
            return "userservice/create-user";
        }

        try {
            userService.createUser(dto);
            return "redirect:/api/users?success=User created";

        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
            return "userservice/create-user";
        }
    }

    // ================= UPDATE =================

    @GetMapping({"/api/users/edit", "/api/users/{id}/edit"})
    public String updateUserByIdPage(@PathVariable(required = false) Integer id,
                                     Model model,
                                     @RequestParam(required = false) String success) {
        if (success != null) model.addAttribute("success", success);

        if (id != null) {
            try {
                UserResponseDTO res = userService.getUserById(id);
                UserRequestDTO dto = new UserRequestDTO();
                dto.setUserId(res.getUserId());
                dto.setUsername(res.getUsername());
                dto.setEmail(res.getEmail());
                dto.setFullName(res.getFullName());
                dto.setPassword("dummy");
                model.addAttribute("user", dto);
            } catch (Exception e) {
                model.addAttribute("error", "User not found with ID: " + id);
            }
        }
        return "userservice/update-user-by-id";
    }

    @PostMapping("/api/users/{id}/edit")
    public String updateUser(@PathVariable Integer id,
                             @Valid @ModelAttribute("user") UserRequestDTO dto,
                             BindingResult result,
                             Model model) {

        if (result.hasErrors()) {
            return "userservice/update-user";
        }

        try {
            dto.setPassword(null); // Prevent admin from accidentally overwriting user's password
            userService.updateUser(id, dto);
            return "redirect:/api/users?success=Updated";

        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
            return "userservice/update-user";
        }
    }

    // ================= DELETE =================

    @GetMapping({"/api/users/delete", "/api/users/{id}/delete"})
    public String deleteUserByIdPage(@PathVariable(required = false) Integer id,
                                     Model model,
                                     @RequestParam(required = false) String success) {
        if (success != null) model.addAttribute("success", success);

        if (id != null) {
            try {
                model.addAttribute("user", userService.getUserById(id));
            } catch (Exception e) {
                model.addAttribute("error", "User not found with ID: " + id);
            }
        }

        return "userservice/delete-user-by-id";
    }

    @PostMapping("/api/users/delete-action")
    public String deleteUserAction(@RequestParam Integer id) {
        try {
            userService.deleteUser(id);
            return "redirect:/api/users/delete?success=User+" + id + "+deleted";
        } catch (Exception e) {
            return "redirect:/api/users/delete?error=" + java.net.URLEncoder.encode(extractErrorMessage(e), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/api/users/{id}/delete")
    public String deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return "redirect:/api/users?success=Deleted";
        } catch (Exception e) {
            return "redirect:/api/users?error=" + java.net.URLEncoder.encode(extractErrorMessage(e), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    // ================= SEARCH =================

    @GetMapping("/api/users/search")
    public String searchUser(@RequestParam(required = false) String username,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        if (username == null || username.isBlank()) {
            return "userservice/user-search";
        }

        model.addAttribute("searchParam", username);
        model.addAttribute("currentPage", page);
        try {
            Page<UserResponseDTO> userPage = userService.searchUsers(username, page, 10);
            model.addAttribute("users", userPage.getContent());
            model.addAttribute("totalPages", userPage.getTotalPages());
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
            model.addAttribute("users", List.of());
            model.addAttribute("totalPages", 0);
        }
        return "userservice/user-search";
    }

    // ================= USER BY ID =================

    @GetMapping({"/api/users/find", "/api/users/{id}"})
    public String getUserById(@PathVariable(required = false) Integer id,
                              @RequestParam(required = false, name = "id") Integer paramId,
                              Model model) {
        if (id == null && paramId != null) {
            return "redirect:/api/users/" + paramId;
        }

        if (id == null) {
            return "userservice/user-by-id";
        }
        
        try {
            model.addAttribute("user", userService.getUserById(id));
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
        }
        return "userservice/user-by-id";
    }

    // ================= ROLE FILTER =================

    @GetMapping("/api/users/role")
    public String roleFilter(@RequestParam(required = false) String role,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        if (role == null || role.isBlank()) {
            return "userservice/user-role";
        }

        model.addAttribute("searchParam", role);
        model.addAttribute("currentPage", page);
        try {
            Page<UserResponseDTO> userPage = userService.getUsersByRole(role, page, 10);
            model.addAttribute("users", userPage.getContent());
            model.addAttribute("totalPages", userPage.getTotalPages());
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
            model.addAttribute("users", List.of());
            model.addAttribute("totalPages", 0);
        }
        return "userservice/user-role";
    }

    // ================= ROLES =================

    @GetMapping("/api/roles")
    public String rolesPage(Model model) {
        try {
            model.addAttribute("roles", userService.getAllRoles());
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
        }
        return "userservice/roles";
    }

    // ================= USER ROLES =================

    @GetMapping({"/api/users/roles", "/api/users/{id}/roles"})
    public String getUserRoles(@PathVariable(required = false) Integer id,
                               @RequestParam(required = false, name = "id") Integer paramId,
                               Model model) {
        if (id == null && paramId != null) {
            return "redirect:/api/users/" + paramId + "/roles";
        }

        if (id == null) {
            return "userservice/user-roles";
        }

        try {
            model.addAttribute("roles", userService.getUserRoles(id));
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
        }
        return "userservice/user-roles";
    }

    // ================= ASSIGN ROLES =================

    @GetMapping({"/api/users/assign-roles", "/api/users/{id}/assign-roles"})
    public String assignRolesByIdPage(@PathVariable(required = false) Integer id,
                                      Model model,
                                      @RequestParam(required = false) String success) {
        if (success != null) model.addAttribute("success", success);
        
        try {
            model.addAttribute("allRoles", userService.getAllRoles());
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
        }

        if (id != null) {
            try {
                model.addAttribute("user", userService.getUserById(id));
            } catch (Exception e) {
                model.addAttribute("error", extractErrorMessage(e));
            }
        }

        return "userservice/assign-roles-by-id";
    }

    @PostMapping("/api/users/{id}/assign-roles")
    public String assignRolesAction(@PathVariable Integer id,
                                    @RequestParam(required = false) List<String> roles) {
        try {
            if (roles == null) roles = List.of();
            userService.assignRolesToUser(id, roles);
            return "redirect:/api/users/" + id + "/assign-roles?success=Roles+updated+successfully";
        } catch (Exception e) {
            return "redirect:/api/users/" + id + "/assign-roles?error=" + java.net.URLEncoder.encode(extractErrorMessage(e), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    // ================= MY PROFILE =================

    @GetMapping("/api/users/me")
    public String myProfile(Model model, @RequestParam(required = false) String success) {
        if (success != null) {
            model.addAttribute("success", success);
        }

        try {
            UserResponseDTO currentUser = userService.getCurrentUser();
            UserRequestDTO dto = new UserRequestDTO();
            dto.setUsername(currentUser.getUsername());
            dto.setEmail(currentUser.getEmail());
            dto.setFullName(currentUser.getFullName());
            dto.setPassword("");
            model.addAttribute("user", dto);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load profile. Please log in again.");
            return "redirect:/login";
        }
        return "userservice/my-profile";
    }

    @PostMapping("/api/users/me")
    public String updateMyProfile(@ModelAttribute("user") UserRequestDTO dto, Model model) {
        try {
            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                dto.setPassword(null); // Leave password unchanged in backend
            }
            userService.updateCurrentUser(dto);
            return "redirect:/api/users/me?success=Profile updated successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile: " + e.getMessage());
            return "userservice/my-profile";
        }
    }

}