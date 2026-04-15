package com.task.user.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.task.user.dto.UserRequestDTO;
import com.task.user.dto.UserResponseDTO;
import com.task.user.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/users")
	public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO dto) {
		return userService.createUser(dto);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/users/{id}")
	public UserResponseDTO updateUser(@PathVariable Integer id, @Valid @RequestBody UserRequestDTO dto) {
		return userService.updateUser(id, dto);
	}

	@GetMapping("/users")
	public List<UserResponseDTO> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/users/{id}")
	public UserResponseDTO getUserById(@PathVariable Integer id) {
		return userService.getUserById(id);
	}

	@GetMapping("/users/me")
	public UserResponseDTO getCurrentUser(Authentication authentication) {
		return userService.getUserByUsername(authentication.getName());
	}

	@GetMapping("/users/search")
	public List<UserResponseDTO> searchUsers(@RequestParam String username) {
		return userService.searchUsers(username);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/user/dashboard")
	public String userDashboard() {
		return "User Dashboard Access Granted";
	}

	@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
	@GetMapping("/admin")
	public String adminAccess() {
		return "Admin/Manager Access Granted";
	}

	@GetMapping("/users/role")
	public List<UserResponseDTO> getUsersByRole(@RequestParam String role) {
		return userService.getUsersByRole(role);
	}
}