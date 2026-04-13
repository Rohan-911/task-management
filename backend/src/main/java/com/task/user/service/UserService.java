package com.task.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.task.user.dto.UserRequestDTO;
import com.task.user.dto.UserResponseDTO;
import com.task.user.entity.User;
import com.task.user.exception.BadRequestException;
import com.task.user.exception.DuplicateResourceException;
import com.task.user.exception.ResourceNotFoundException;
import com.task.user.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	
	public UserResponseDTO createUser(UserRequestDTO dto) {

		if (dto.getUserId() == null) {
			throw new BadRequestException("UserID is required");
		}

		if (userRepository.existsById(dto.getUserId())) {
			throw new DuplicateResourceException("UserID already exists");
		}

		if (userRepository.existsByUsername(dto.getUsername())) {
			throw new DuplicateResourceException("Username already exists");
		}

		if (dto.getFullName() == null || dto.getFullName().isBlank()) {
			throw new BadRequestException("Full Name is required");
		}

		User user = new User();

		user.setUserId(dto.getUserId());
		user.setUsername(dto.getUsername());
		user.setPassword(dto.getPassword());
		user.setEmail(dto.getEmail());
		user.setFullName(dto.getFullName());

		User saved = userRepository.save(user);

		return mapToDTO(saved);
	}

	
	public UserResponseDTO updateUser(Integer id, UserRequestDTO dto) {

		User user = userRepository.findByIdWithRoles(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

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
}