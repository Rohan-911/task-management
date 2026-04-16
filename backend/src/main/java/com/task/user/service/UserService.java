package com.task.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private UserRolesRepository userRolesRepository; 

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

		User user = new User();
		user.setUserId(dto.getUserId());
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword());
		user.setEmail(dto.getEmail());
		user.setFullName(dto.getFullName());

		User savedUser = userRepository.save(user);

		UserRole role = userRoleRepository.findByRoleNameIgnoreCase("user")
				.orElseThrow(() -> new RuntimeException("Default role USER not found in DB"));

		UserRoles userRoles = new UserRoles();

		UserRolesId id = new UserRolesId();
		id.setUserId(savedUser.getUserId());
		id.setUserRoleId(role.getUserRoleId());

		userRoles.setId(id);
		userRoles.setUser(savedUser);
		userRoles.setRole(role);

		userRolesRepository.save(userRoles);

		return mapToDTO(savedUser);
	}

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

		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword());
		user.setEmail(dto.getEmail());
		user.setFullName(dto.getFullName());

		User updated = userRepository.save(user);

		return mapToDTO(updated);
	}

	public List<UserResponseDTO> getAllUsers() {
		return userRepository.findAllUsersWithRoles().stream().map(this::mapToDTO).toList();
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

	public List<UserResponseDTO> searchUsers(String username) {
		return userRepository.findByUsernameContainingIgnoreCase(username).stream().map(this::mapToDTO).toList();
	}

	private UserResponseDTO mapToDTO(User user) {

		List<String> roles = (user.getUserRoles() != null)
				? user.getUserRoles().stream().map(ur -> ur.getRole().getRoleName()).toList()
				: List.of();

		return new UserResponseDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getFullName(), roles);
	}

	public List<UserResponseDTO> getUsersByRole(String role) {

		if (role == null || role.isBlank()) {
			throw new BadRequestException("Role is required");
		}

		List<User> users = userRepository.findUsersByRole(role);

		if (users.isEmpty()) {
			throw new ResourceNotFoundException("No users found with role: " + role);
		}

		return users.stream().map(this::mapToDTO).toList();
	}
}