package com.task.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.user.dto.UserRequestDTO;
import com.task.user.dto.UserResponseDTO;
import com.task.user.entity.User;
import com.task.user.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	// ✅ CREATE USER
	public User createUser(UserRequestDTO dto) {

		if (userRepository.existsByUsername(dto.getUsername())) {
			throw new RuntimeException("Username already exists");
		}

		User user = new User();
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword()); // as per DB
		user.setEmail(dto.getEmail());
		user.setFullName(dto.getFullName());

		return userRepository.save(user);
	}

	// 🔥 DTO MAPPER
	private UserResponseDTO mapToDTO(User user) {

		List<String> roles = (user.getUserRoles() != null)
				? user.getUserRoles().stream().map(ur -> ur.getRole().getRoleName()).toList()
				: List.of();

		return new UserResponseDTO(user.getUserId(), user.getUsername(), user.getEmail(), user.getFullName(), roles);
	}

	// ✅ GET ALL USERS
	public List<UserResponseDTO> getAllUsers() {
		return userRepository.findAllUsersWithRoles().stream().map(this::mapToDTO).toList();
	}

	// ✅ GET USER BY ID
	public UserResponseDTO getUserById(Integer id) {
		User user = userRepository.findByIdWithRoles(id)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

		return mapToDTO(user);
	}

	// ✅ GET USER BY USERNAME
	public UserResponseDTO getUserByUsername(String username) {
		User user = userRepository.findByUsernameWithRoles(username)
				.orElseThrow(() -> new RuntimeException("User not found with username: " + username));

		return mapToDTO(user);
	}

	// ✅ SEARCH USERS
	public List<UserResponseDTO> searchUsers(String username) {
		return userRepository.findByUsernameContainingIgnoreCase(username).stream().map(this::mapToDTO).toList();
	}
}