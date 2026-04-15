package com.task.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

import com.task.user.dto.UserRequestDTO;
import com.task.user.entity.*;
import com.task.user.exception.BadRequestException;
import com.task.user.exception.DuplicateResourceException;
import com.task.user.exception.ResourceNotFoundException;
import com.task.user.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserRoleRepository userRoleRepository;

	@Mock
	private UserRolesRepository userRolesRepository;

	@InjectMocks
	private UserService userService;

	private UserRequestDTO dto;

	@BeforeEach
	void setup() {
		dto = new UserRequestDTO();
		dto.setUserId(1);
		dto.setUsername("john");
		dto.setPassword("1234");
		dto.setEmail("john@mail.com");
		dto.setFullName("John Doe");
	}

	@Test
	void createUser_success() {

		when(userRepository.existsById(1)).thenReturn(false);
		when(userRepository.existsByUsername("john")).thenReturn(false);

		User savedUser = new User();
		savedUser.setUserId(1);
		savedUser.setUsername("john");

		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		UserRole role = new UserRole();
		role.setUserRoleId(2);
		role.setRoleName("USER");

		when(userRoleRepository.findByRoleNameIgnoreCase("user")).thenReturn(Optional.of(role));

		var response = userService.createUser(dto);

		assertNotNull(response);
		assertEquals("john", response.getUsername());

		verify(userRolesRepository, times(1)).save(any());
	}

	@Test
	void createUser_duplicateUsername() {
		when(userRepository.existsById(1)).thenReturn(false);
		when(userRepository.existsByUsername("john")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> userService.createUser(dto));
	}

	@Test
	void createUser_duplicateUserId() {
		when(userRepository.existsById(1)).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> userService.createUser(dto));
	}

	@Test
	void createUser_missingUserId() {
		dto.setUserId(null);

		assertThrows(BadRequestException.class, () -> userService.createUser(dto));
	}

	@Test
	void createUser_missingUsername() {
		dto.setUsername("");

		assertThrows(BadRequestException.class, () -> userService.createUser(dto));
	}

	@Test
	void createUser_missingEmail() {
		dto.setEmail("");

		assertThrows(BadRequestException.class, () -> userService.createUser(dto));
	}

	@Test
	void createUser_missingFullName() {
		dto.setFullName("");

		assertThrows(BadRequestException.class, () -> userService.createUser(dto));
	}

	@Test
	void createUser_roleNotFound() {

		when(userRepository.existsById(1)).thenReturn(false);
		when(userRepository.existsByUsername("john")).thenReturn(false);
		when(userRepository.save(any(User.class))).thenReturn(new User());

		when(userRoleRepository.findByRoleNameIgnoreCase("user")).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> userService.createUser(dto));
	}

	@Test
	void getUserById_success() {
		User user = new User();
		user.setUserId(1);

		when(userRepository.findByIdWithRoles(1)).thenReturn(Optional.of(user));

		var result = userService.getUserById(1);

		assertEquals(1, result.getUserId());
	}

	@Test
	void getUserById_notFound() {
		when(userRepository.findByIdWithRoles(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1));
	}

	@Test
	void getUserByUsername_success() {
		User user = new User();
		user.setUsername("john");

		when(userRepository.findByUsernameWithRoles("john")).thenReturn(Optional.of(user));

		var result = userService.getUserByUsername("john");

		assertEquals("john", result.getUsername());
	}

	@Test
	void getUserByUsername_notFound() {
		when(userRepository.findByUsernameWithRoles("john")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("john"));
	}

	@Test
	void getAllUsers_success() {

		when(userRepository.findAllUsersWithRoles()).thenReturn(List.of(new User(), new User()));

		var result = userService.getAllUsers();

		assertEquals(2, result.size());
	}

	@Test
	void searchUsers_success() {

		when(userRepository.findByUsernameContainingIgnoreCase("john")).thenReturn(List.of(new User()));

		var result = userService.searchUsers("john");

		assertNotNull(result);
	}

	@Test
	void updateUser_success() {

		User user = new User();
		user.setUserId(1);
		user.setUsername("old");

		when(userRepository.findByIdWithRoles(1)).thenReturn(Optional.of(user));

		when(userRepository.save(any(User.class))).thenReturn(user);

		dto.setUsername("new");

		var result = userService.updateUser(1, dto);

		assertEquals("new", result.getUsername());
	}

	@Test
	void updateUser_notFound() {

		when(userRepository.findByIdWithRoles(1)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1, dto));
	}

	@Test
	void getUsersByRole_success() {

		when(userRepository.findUsersByRole("ADMIN")).thenReturn(List.of(new User()));

		var result = userService.getUsersByRole("ADMIN");

		assertNotNull(result);
	}

	@Test
	void getUsersByRole_notFound() {

		when(userRepository.findUsersByRole("ADMIN")).thenReturn(List.of());

		assertThrows(ResourceNotFoundException.class, () -> userService.getUsersByRole("ADMIN"));
	}
}