package com.task.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.task.user.dto.AuthResponseDTO;
import com.task.user.dto.LoginRequestDTO;
import com.task.user.entity.User;
import com.task.user.entity.UserRoles;
import com.task.user.repository.UserRepository;
import com.task.user.security.JwtUtil;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Transactional
	public AuthResponseDTO login(LoginRequestDTO dto) {

		User user = userRepository.findByUsernameWithRoles(dto.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (!dto.getPassword().equals(user.getPassword())) {
			throw new RuntimeException("Invalid password");
		}

		List<String> roles;

		if (user.getUserRoles() != null && !user.getUserRoles().isEmpty()) {
			roles = user.getUserRoles().stream().map(UserRoles::getRole).filter(role -> role != null)
					.map(role -> role.getRoleName()).toList();
		} else {
			roles = List.of();
		}

		System.out.println("USERNAME: " + user.getUsername());
		System.out.println("ROLES FROM DB: " + roles);

		List<String> springRoles = roles.stream().map(role -> "ROLE_" + role.toUpperCase()).toList();

		System.out.println("SPRING ROLES: " + springRoles);

		String token = jwtUtil.generateToken(user.getUsername(), springRoles);

		return new AuthResponseDTO(token, user.getUserId(), roles);
	}
}