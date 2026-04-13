package com.task.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.task.user.entity.User;
import com.task.user.repository.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers() {

        User user = new User();
        user.setUsername("john");

        when(userRepository.findAllUsersWithRoles())
                .thenReturn(List.of(user));

        var result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void getUserByIdSuccess() {

        User user = new User();
        user.setUserId(1);

        when(userRepository.findByIdWithRoles(1))
                .thenReturn(Optional.of(user));

        var result = userService.getUserById(1);

        assertEquals(1, result.getUserId());
    }

    @Test
    void getUserByIdNotFound() {

        when(userRepository.findByIdWithRoles(1))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.getUserById(1));
    }
}