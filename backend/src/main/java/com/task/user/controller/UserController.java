package com.task.user.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.task.user.dto.UserRequestDTO;
import com.task.user.dto.UserResponseDTO;
import com.task.user.service.UserService;

import jakarta.validation.Valid;

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
	public Page<UserResponseDTO> getAllUsers(@RequestParam(defaultValue = "0") int page) {
		return userService.getAllUsers(page, 10);
	}

	@GetMapping("/users/{id}")
	public UserResponseDTO getUserById(@PathVariable Integer id) {
		return userService.getUserById(id);
	}

	@GetMapping("/users/me")
	public UserResponseDTO getCurrentUser(Authentication authentication) {
		return userService.getUserByUsername(authentication.getName());
	}

	@PutMapping("/users/me")
	public UserResponseDTO updateCurrentUser(Authentication authentication, @RequestBody UserRequestDTO dto) {
		UserResponseDTO currentUser = userService.getUserByUsername(authentication.getName());
		
		// Security: Prevent role escalation by ignoring any roles sent in the payload
		dto.setRoles(null);
		
		// Security: Force the ID to match the authenticated user so they can't tamper with it
		dto.setUserId(currentUser.getUserId());
		
		return userService.updateUser(currentUser.getUserId(), dto);
	}

	@GetMapping("/users/search")
	public Page<UserResponseDTO> searchUsers(@RequestParam String username,
	                                         @RequestParam(defaultValue = "0") int page) {
		return userService.searchUsers(username, page, 10);
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
	public Page<UserResponseDTO> getUsersByRole(@RequestParam String role,
	                                            @RequestParam(defaultValue = "0") int page) {
		return userService.getUsersByRole(role, page, 10);
	}
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/users/{id}/roles")
	public String assignRoles(@PathVariable Integer id,
	                          @RequestBody List<String> roles) {

	    userService.assignRolesToUser(id, roles);
	    return "Roles updated successfully";
	}
	// ================= GET ALL ROLES =================
	@GetMapping("/roles")
	public List<String> getAllRoles() {
	    return userService.fetchAllRoles();
	}

	// ================= GET USER ROLES =================
	@GetMapping("/users/{id}/roles")
	public List<String> getUserRoles(@PathVariable Integer id) {
	    return userService.getUserRoles(id);
	}

	// ================= DELETE USER =================
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/users/{id}")
	public String deleteUser(@PathVariable Integer id) {

	    userService.deleteUser(id);
	    return "User deleted successfully";
	}
	
}