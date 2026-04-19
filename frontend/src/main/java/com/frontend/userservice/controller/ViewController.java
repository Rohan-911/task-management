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

    @GetMapping({"/", "/login"})
    public String home() {
        return "userservice/login";
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

    @GetMapping("/users-view")
    public String usersPage(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(required = false) String success) {

        int size = 10;

        Page<UserResponseDTO> userPage = userService.getAllUsers(page, size);

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("allRoles", userService.getAllRoles());

        if (success != null) {
            model.addAttribute("success", success);
        }

        return "userservice/users";
    }

    // ================= CREATE =================

    @GetMapping("/create-user")
    public String createUserPage(Model model) {
        model.addAttribute("userRequestDTO", new UserRequestDTO());
        return "userservice/create-user";
    }

    @PostMapping("/create-user")
    public String createUser(@Valid UserRequestDTO dto,
                             BindingResult result,
                             Model model) {

        model.addAttribute("userRequestDTO", dto);

        if (result.hasErrors()) {
            return "userservice/create-user";
        }

        try {
            userService.createUser(dto);
            return "redirect:/users-view?success=User created";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "userservice/create-user";
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
                dto.setPassword("dummy");
                model.addAttribute("user", dto);
            } catch (Exception e) {
                model.addAttribute("error", "User not found with ID: " + id);
            }
        }
        return "userservice/update-user-by-id";
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

        return "userservice/update-user";
    }

    @PostMapping("/update-user/{id}")
    public String updateUser(@PathVariable Integer id,
                             @Valid @ModelAttribute("user") UserRequestDTO dto,
                             BindingResult result,
                             Model model) {

        if (result.hasErrors()) {
            return "userservice/update-user";
        }

        try {
            userService.updateUser(id, dto);
            return "redirect:/users-view?success=Updated";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "userservice/update-user";
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

        return "userservice/delete-user-by-id";
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
        return "userservice/user-search";
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
        return "userservice/user-search";
    }

    // ================= USER BY ID =================

    @GetMapping("/user-by-id")
    public String userByIdPage() {
        return "userservice/user-by-id";
    }

    @GetMapping("/user-by-id/result")
    public String getUserById(@RequestParam Integer id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "userservice/user-by-id";
    }

    // ================= ROLE FILTER =================

    @GetMapping("/user-role")
    public String rolePage() {
        return "userservice/user-role";
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
        return "userservice/user-role";
    }

    // ================= ROLES =================

    @GetMapping("/roles-view")
    public String rolesPage(Model model) {
        model.addAttribute("roles", userService.getAllRoles());
        return "userservice/roles";
    }

    // ================= USER ROLES =================

    @GetMapping("/user-roles")
    public String userRolesPage() {
        return "userservice/user-roles";
    }

    @GetMapping("/user-roles/result")
    public String getUserRoles(@RequestParam Integer id, Model model) {
        model.addAttribute("roles", userService.getUserRoles(id));
        return "userservice/user-roles";
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

        return "userservice/assign-roles-by-id";
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
            dto.setPassword("");
            model.addAttribute("user", dto);
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load profile. Please log in again.");
            return "redirect:/";
        }
        return "userservice/my-profile";
    }

    @PostMapping("/current-user")
    public String updateMyProfile(@ModelAttribute("user") UserRequestDTO dto, Model model) {
        try {
            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                dto.setPassword("dummyPassword");
            }
            userService.updateCurrentUser(dto);
            return "redirect:/current-user?success=Profile updated successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile: " + e.getMessage());
            return "userservice/my-profile";
        }
    }
}