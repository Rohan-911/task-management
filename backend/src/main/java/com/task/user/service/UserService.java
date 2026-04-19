package com.task.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.task.exception.BadRequestException;
import com.task.exception.DuplicateResourceException;
import com.task.exception.ResourceNotFoundException;
import com.task.user.dto.UserRequestDTO;
import com.task.user.dto.UserResponseDTO;
import com.task.user.entity.User;
import com.task.user.entity.UserRole;
import com.task.user.entity.UserRoles;
import com.task.user.entity.UserRolesId;
import com.task.user.repository.UserRepository;
import com.task.user.repository.UserRoleRepository;
import com.task.user.repository.UserRolesRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private UserRolesRepository userRolesRepository;

	// ================= CREATE USER =================
	public UserResponseDTO createUser(UserRequestDTO dto) {

		if (dto.getUsername() != null)
			dto.setUsername(dto.getUsername().trim().toLowerCase());

		if (dto.getEmail() != null)
			dto.setEmail(dto.getEmail().trim().toLowerCase());

		if (dto.getFullName() != null)
			dto.setFullName(dto.getFullName().trim());

		if (dto.getUserId() == null)
			throw new BadRequestException("UserID is required");

		if (dto.getUsername() == null || dto.getUsername().isBlank())
			throw new BadRequestException("Username is required");

		if (dto.getPassword() == null || dto.getPassword().isBlank())
			throw new BadRequestException("Password is required");

		if (dto.getEmail() == null || dto.getEmail().isBlank())
			throw new BadRequestException("Email is required");

		if (dto.getFullName() == null || dto.getFullName().isBlank())
			throw new BadRequestException("Full Name is required");

		if (userRepository.existsById(dto.getUserId()))
			throw new DuplicateResourceException("UserID already exists");

		if (userRepository.existsByUsername(dto.getUsername()))
			throw new DuplicateResourceException("Username already exists");

		if (userRepository.existsByEmail(dto.getEmail()))
			throw new DuplicateResourceException("Email already registered");

		User user = new User();
		user.setUserId(dto.getUserId());
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword());
		user.setEmail(dto.getEmail());
		user.setFullName(dto.getFullName());

		User savedUser = userRepository.save(user);

		// 🔥 ROLE LOGIC (UPDATED)
		List<String> roleNames = dto.getRoles();

		if (roleNames == null || roleNames.isEmpty()) {
			roleNames = List.of("user"); // default role
		}

		List<UserRoles> userRolesList = roleNames.stream().map(roleName -> {

			UserRole role = userRoleRepository.findByRoleNameIgnoreCase(roleName)
					.orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

			UserRoles userRole = new UserRoles();

			UserRolesId id = new UserRolesId();
			id.setUserId(savedUser.getUserId());
			id.setUserRoleId(role.getUserRoleId());

			userRole.setId(id);
			userRole.setUser(savedUser);
			userRole.setRole(role);

			return userRole;

		}).toList();

		userRolesRepository.saveAll(userRolesList);

		return mapToDTO(savedUser);
	}

	// ================= UPDATE USER =================
	public UserResponseDTO updateUser(Integer id, UserRequestDTO dto) {

		User user = userRepository.findByIdWithRoles(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

		if (dto.getUsername() != null)
			dto.setUsername(dto.getUsername().trim().toLowerCase());

		if (dto.getEmail() != null)
			dto.setEmail(dto.getEmail().trim().toLowerCase());

		if (dto.getFullName() != null)
			dto.setFullName(dto.getFullName().trim());

		if (dto.getUsername() == null || dto.getUsername().isBlank())
			throw new BadRequestException("Username cannot be empty");

		if (dto.getEmail() == null || dto.getEmail().isBlank())
			throw new BadRequestException("Email cannot be empty");

		if (!user.getUsername().equals(dto.getUsername()) && userRepository.existsByUsername(dto.getUsername()))
			throw new DuplicateResourceException("Username already exists");

		if (!user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail()))
			throw new DuplicateResourceException("Email already registered");

		if (!dto.getEmail().matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
			throw new RuntimeException("Invalid email format");
		}

		user.setUsername(dto.getUsername());
		if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
			user.setPassword(dto.getPassword());
		}
		user.setEmail(dto.getEmail());
		user.setFullName(dto.getFullName());

		User updated = userRepository.save(user);

		// 🔥 ROLE UPDATE (SAFE)
		if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {

			if (user.getUserRoles() != null && !user.getUserRoles().isEmpty()) {
				userRolesRepository.deleteAll(user.getUserRoles());
				user.getUserRoles().clear();
			}

			List<UserRoles> newRoles = dto.getRoles().stream().map(roleName -> {

				UserRole role = userRoleRepository.findByRoleNameIgnoreCase(roleName)
						.orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

				UserRoles ur = new UserRoles();

				UserRolesId roleId = new UserRolesId(user.getUserId(), role.getUserRoleId());

				ur.setId(roleId);
				ur.setUser(user);
				ur.setRole(role);

				return ur;

			}).toList();

			userRolesRepository.saveAll(newRoles);
		}

		return mapToDTO(updated);
	}

	// ================= GET ALL =================
	public Page<UserResponseDTO> getAllUsers(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
		return userRepository.findAll(pageable).map(this::mapToDTO);
	}

	public UserResponseDTO getUserById(Integer id) {
		User user = userRepository.findByIdWithRoles(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

		return mapToDTO(user);
	}

	public UserResponseDTO getUserByUsername(String username) {
		User user = userRepository.findByUsernameWithRoles(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

		return mapToDTO(user);
	}

	public Page<UserResponseDTO> searchUsers(String username, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
		return userRepository.findByUsernameContainingIgnoreCase(username, pageable).map(this::mapToDTO);
	}

	private UserResponseDTO mapToDTO(User user) {

		List<String> roles = (user.getUserRoles() != null)
				? user.getUserRoles().stream().map(ur -> ur.getRole().getRoleName()).toList()
				: List.of();

		return new UserResponseDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getFullName(), roles);
	}

	public Page<UserResponseDTO> getUsersByRole(String role, int page, int size) {

		if (role == null || role.isBlank()) {
			throw new BadRequestException("Role is required");
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
		Page<User> users = userRepository.findUsersByRole(role, pageable);

		if (users.isEmpty() && page == 0) {
			throw new ResourceNotFoundException("No users found with role: " + role);
		}

		return users.map(this::mapToDTO);
	}

	// ================= ASSIGN ROLES =================
	@Transactional
	public void assignRolesToUser(Integer userId, List<String> roleNames) {

		User user = userRepository.findByIdWithRoles(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		if (user.getUserRoles() != null && !user.getUserRoles().isEmpty()) {
			userRolesRepository.deleteAll(user.getUserRoles());
			user.getUserRoles().clear();
		}

		List<UserRoles> newRoles = roleNames.stream().map(roleName -> {

			UserRole role = userRoleRepository.findByRoleNameIgnoreCase(roleName)
					.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

			UserRoles userRole = new UserRoles();

			UserRolesId id = new UserRolesId(userId, role.getUserRoleId());

			userRole.setId(id);
			userRole.setUser(user);
			userRole.setRole(role);

			return userRole;

		}).toList();

		userRolesRepository.saveAll(newRoles);
	}

	public List<String> getAllRoles() {
		return userRoleRepository.findAll().stream().map(UserRole::getRoleName).toList();
	}
	// ================= GET ALL ROLES =================
	public List<String> fetchAllRoles() {
	    return userRoleRepository.findAll()
	            .stream()
	            .map(UserRole::getRoleName)
	            .toList();
	}

	// ================= GET USER ROLES =================
	public List<String> getUserRoles(Integer userId) {

	    User user = userRepository.findByIdWithRoles(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    return (user.getUserRoles() != null)
	            ? user.getUserRoles()
	                  .stream()
	                  .map(ur -> ur.getRole().getRoleName())
	                  .toList()
	            : List.of();
	}

	// ================= DELETE USER =================
	@Transactional
	public void deleteUser(Integer userId) {

	    User user = userRepository.findByIdWithRoles(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    // delete role mappings first (IMPORTANT)
	    if (user.getUserRoles() != null && !user.getUserRoles().isEmpty()) {
	        userRolesRepository.deleteAll(user.getUserRoles());
	        user.getUserRoles().clear();
	    }

	    userRepository.delete(user);
	}
}