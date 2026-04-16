package com.task.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.task.user.dto.LoginRequestDTO;
import com.task.user.entity.User;
import com.task.user.entity.UserRole;
import com.task.user.entity.UserRoles;
import com.task.user.repository.UserRepository;
import com.task.user.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private AuthService authService;

	private LoginRequestDTO dto;

	@BeforeEach
	void setup() {
		dto = new LoginRequestDTO();
		dto.setUsername("john");
		dto.setPassword("1234");
	}

	@Test
	void login_success_withRoles() {

		User user = new User();
		user.setUserId(1);
		user.setUsername("john");
		user.setPassword("1234");

		UserRole role = new UserRole();
		role.setRoleName("ADMIN");

		UserRoles ur = new UserRoles();
		ur.setRole(role);

		user.setUserRoles(List.of(ur));

		when(userRepository.findByUsernameWithRoles("john")).thenReturn(Optional.of(user));

		when(jwtUtil.generateToken(anyString(), anyList())).thenReturn("mocked-token");

		var response = authService.login(dto);

		assertNotNull(response);
		assertEquals("mocked-token", response.getToken());
		assertEquals(1, response.getUserId());
		assertEquals(List.of("ADMIN"), response.getRoles());

		verify(jwtUtil, times(1)).generateToken(eq("john"), anyList());
	}

	@Test
	void login_success_noRoles() {

		User user = new User();
		user.setUserId(2);
		user.setUsername("john");
		user.setPassword("1234");
		user.setUserRoles(List.of());
		when(userRepository.findByUsernameWithRoles("john")).thenReturn(Optional.of(user));

		when(jwtUtil.generateToken(anyString(), anyList())).thenReturn("token");

		var response = authService.login(dto);

		assertNotNull(response);
		assertEquals(List.of(), response.getRoles());
	}

	@Test
	void login_roleIsNull_shouldNotFail() {

		User user = new User();
		user.setUserId(3);
		user.setUsername("john");
		user.setPassword("1234");

		UserRoles ur = new UserRoles();
		ur.setRole(null); // null role

		user.setUserRoles(List.of(ur));

		when(userRepository.findByUsernameWithRoles("john")).thenReturn(Optional.of(user));

		when(jwtUtil.generateToken(anyString(), anyList())).thenReturn("token");

		var response = authService.login(dto);

		assertNotNull(response);
		assertEquals(List.of(), response.getRoles());
	}

	@Test
	void login_userNotFound() {

		when(userRepository.findByUsernameWithRoles("john")).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> authService.login(dto));
	}

	@Test
	void login_invalidPassword() {

		User user = new User();
		user.setUsername("john");
		user.setPassword("wrong");

		when(userRepository.findByUsernameWithRoles("john")).thenReturn(Optional.of(user));

		assertThrows(RuntimeException.class, () -> authService.login(dto));
	}
}