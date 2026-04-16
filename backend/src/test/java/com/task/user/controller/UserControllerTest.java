package com.task.user.controller;

import com.task.user.dto.UserRequestDTO;
import com.task.user.dto.UserResponseDTO;
import com.task.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRequestDTO dto;
    private UserResponseDTO response;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        dto = new UserRequestDTO();
        dto.setUserId(1);
        dto.setUsername("john");
        dto.setPassword("1234");
        dto.setEmail("john@mail.com");
        dto.setFullName("John Doe");

        response = new UserResponseDTO(1, "john", "john@mail.com", "John Doe", List.of("USER"));
    }

    @Test
    void createUser_success() {
        when(userService.createUser(dto)).thenReturn(response);

        UserResponseDTO result = userController.createUser(dto);

        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    @Test
    void updateUser_success() {
        when(userService.updateUser(1, dto)).thenReturn(response);

        UserResponseDTO result = userController.updateUser(1, dto);

        assertEquals(1, result.getUserId());
    }

    @Test
    void getAllUsers_success() {
        when(userService.getAllUsers()).thenReturn(List.of(response));

        List<UserResponseDTO> result = userController.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void getUserById_success() {
        when(userService.getUserById(1)).thenReturn(response);

        UserResponseDTO result = userController.getUserById(1);

        assertEquals("john", result.getUsername());
    }

    @Test
    void searchUsers_success() {
        when(userService.searchUsers("john")).thenReturn(List.of(response));

        List<UserResponseDTO> result = userController.searchUsers("john");

        assertFalse(result.isEmpty());
    }

    @Test
    void getUsersByRole_success() {
        when(userService.getUsersByRole("USER")).thenReturn(List.of(response));

        List<UserResponseDTO> result = userController.getUsersByRole("USER");

        assertEquals(1, result.size());
    }
}