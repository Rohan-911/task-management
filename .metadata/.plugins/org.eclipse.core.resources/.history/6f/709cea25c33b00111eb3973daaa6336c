package com.task.frontend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.task.frontend.dto.UserRequestDTO;
import com.task.frontend.dto.UserResponseDTO;
import com.task.frontend.service.FrontendUserService;

import jakarta.validation.Valid;

@Controller
public class ViewController {

    private final FrontendUserService userService;

    public ViewController(FrontendUserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/", "/login"})
    public String home() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(jakarta.servlet.http.HttpServletRequest request) {
        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/user-dashboard")
    public String userDashboard() {
        return "user-dashboard";
    }

    // ================= USERS =================

    @GetMapping("/users-view")
    public String usersPage(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String success) {

        Page<UserResponseDTO> userPage = userService.getAllUsers(page, 10);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("allRoles", userService.getAllRoles());

        if (success != null) {
            model.addAttribute("success", success);
        }

        return "users";
    }

    // ================= CREATE =================

    @GetMapping("/create-user")
    public String createUserPage(Model model) {
        model.addAttribute("userRequestDTO", new UserRequestDTO());
        return "create-user";
    }

    @PostMapping("/create-user")
    public String createUser(@Valid UserRequestDTO dto,
                             BindingResult result,
                             Model model) {

        model.addAttribute("userRequestDTO", dto);

        if (result.hasErrors()) {
            return "create-user";
        }

        try {
            userService.createUser(dto);
            return "redirect:/users-view?success=User created";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "create-user";
        }
    }

    // ================= UPDATE =================

    @GetMapping({"/update-user-by-id", "/update-user-by-id/{id}"})
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
                dto.setPassword("dummy"); // Backend expects password, provide dummy or let frontend handle it
                model.addAttribute("user", dto);
            } catch (Exception e) {
                model.addAttribute("error", "User not found with ID: " + id);
            }
        }
        return "update-user-by-id";
    }

    @GetMapping("/update-user/{id}")
    public String editUser(@PathVariable Integer id, Model model) {

        UserResponseDTO res = userService.getUserById(id);

        UserRequestDTO dto = new UserRequestDTO();
        dto.setUserId(res.getUserId());
        dto.setUsername(res.getUsername());
        dto.setEmail(res.getEmail());
        dto.setFullName(res.getFullName());
        dto.setPassword("dummy");

        model.addAttribute("user", dto);

        return "update-user";
    }

    @PostMapping("/update-user/{id}")
    public String updateUser(@PathVariable Integer id,
                             @Valid @ModelAttribute("user") UserRequestDTO dto,
                             BindingResult result,
                             Model model) {

        if (result.hasErrors()) {
            return "update-user";
        }

        try {
            userService.updateUser(id, dto);
            return "redirect:/users-view?success=Updated";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "update-user";
        }
    }

    // ================= DELETE =================

    @GetMapping({"/delete-user-by-id", "/delete-user-by-id/{id}"})
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
        
        return "delete-user-by-id";
    }

    @PostMapping("/delete-user-action")
    public String deleteUserAction(@RequestParam Integer id) {
        userService.deleteUser(id);
        return "redirect:/delete-user-by-id?success=User+" + id + "+deleted";
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "redirect:/users-view?success=Deleted";
    }

    // ================= SEARCH =================

    @GetMapping("/user-search")
    public String searchPage() {
        return "user-search";
    }

    @GetMapping("/user-search/result")
    public String searchUser(@RequestParam String username, 
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        Page<UserResponseDTO> userPage = userService.searchUsers(username, page, 10);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("searchParam", username);
        return "user-search";
    }

    // ================= USER BY ID =================

    @GetMapping("/user-by-id")
    public String userByIdPage() {
        return "user-by-id";
    }

    @GetMapping("/user-by-id/result")
    public String getUserById(@RequestParam Integer id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "user-by-id";
    }

    // ================= ROLE FILTER =================

    @GetMapping("/user-role")
    public String rolePage() {
        return "user-role";
    }

    @GetMapping("/user-role/result")
    public String roleFilter(@RequestParam String role, 
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        Page<UserResponseDTO> userPage = userService.getUsersByRole(role, page, 10);
        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("searchParam", role);
        return "user-role";
    }

    // ================= ROLES =================

    @GetMapping("/roles-view")
    public String rolesPage(Model model) {
        model.addAttribute("roles", userService.getAllRoles());
        return "roles";
    }

    // ================= USER ROLES =================

    @GetMapping("/user-roles")
    public String userRolesPage() {
        return "user-roles";
    }

    @GetMapping("/user-roles/result")
    public String getUserRoles(@RequestParam Integer id, Model model) {
        model.addAttribute("roles", userService.getUserRoles(id));
        return "user-roles";
    }

    // ================= ASSIGN ROLES =================

    @GetMapping({"/assign-roles-by-id", "/assign-roles-by-id/{id}"})
    public String assignRolesByIdPage(@PathVariable(required = false) Integer id, 
                                      Model model, 
                                      @RequestParam(required = false) String success) {
        if (success != null) model.addAttribute("success", success);
        model.addAttribute("allRoles", userService.getAllRoles());
        
        if (id != null) {
            try {
                model.addAttribute("user", userService.getUserById(id));
            } catch (Exception e) {
                model.addAttribute("error", "User not found with ID: " + id);
            }
        }
        
        return "assign-roles-by-id";
    }

    @PostMapping("/assign-roles-action")
    public String assignRolesAction(@RequestParam Integer id,
                                   @RequestParam(required = false) List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            userService.assignRolesToUser(id, List.of("user"));
            return "redirect:/assign-roles-by-id?success=No+roles+selected,+default+'user'+role+assigned.";
        }
        userService.assignRolesToUser(id, roles);
        return "redirect:/assign-roles-by-id?success=Roles+updated+successfully";
    }

    @PostMapping("/assign-roles/{id}")
    public String assignRolesUI(@PathVariable Integer id,
                               @RequestParam(required = false) List<String> roles) {

        if (roles == null || roles.isEmpty()) {
            userService.assignRolesToUser(id, List.of("user"));
            return "redirect:/users-view?success=No+roles+selected,+default+'user'+role+assigned.";
        }
        userService.assignRolesToUser(id, roles);
        return "redirect:/users-view?success=Roles+updated";
    }

    // ================= MY PROFILE =================
    @GetMapping("/current-user")
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
            dto.setPassword(""); // leave blank by default
            model.addAttribute("user", dto);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load profile. Please log in again.");
            return "redirect:/";
        }
        return "my-profile";
    }

    @PostMapping("/current-user")
    public String updateMyProfile(@ModelAttribute("user") UserRequestDTO dto, Model model) {
        try {
            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                dto.setPassword("dummyPassword"); // Handle backend constraint if needed
            }
            userService.updateCurrentUser(dto);
            return "redirect:/current-user?success=Profile updated successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile: " + e.getMessage());
            return "my-profile";
        }
    }
}
