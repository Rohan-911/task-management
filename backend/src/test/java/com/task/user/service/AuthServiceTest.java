package com.task.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.task.user.dto.LoginRequestDTO;
import com.task.user.entity.User;
import com.task.user.entity.UserRole;
import com.task.user.entity.UserRoles;
import com.task.user.repository.UserRepository;
import com.task.user.security.JwtUtil;

public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loginSuccess() {

      
        User user = new User();
        user.setUserId(1);
        user.setUsername("john_doe");
        user.setPassword("password123");

       
        UserRole role = new UserRole();
        role.setRoleName("User");

        UserRoles ur = new UserRoles();
        ur.setRole(role);

        user.setUserRoles(List.of(ur));

       
        when(userRepository.findByUsernameWithRoles("john_doe"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(anyString(), anyList()))
                .thenReturn("mock_token");

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("john_doe");
        dto.setPassword("password123");

        var response = authService.login(dto);

        assertEquals("mock_token", response.getToken());
        assertEquals(1, response.getUserId());
        assertEquals(1, response.getRoles().size());
    }

    @Test
    void loginUserNotFound() {

       
        when(userRepository.findByUsernameWithRoles("abc"))
                .thenReturn(Optional.empty());

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("abc");
        dto.setPassword("123");

        assertThrows(RuntimeException.class, () -> authService.login(dto));
    }

    @Test
    void loginWrongPassword() {

        User user = new User();
        user.setUsername("john");
        user.setPassword("correct");

        
        when(userRepository.findByUsernameWithRoles("john"))
                .thenReturn(Optional.of(user));

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("john");
        dto.setPassword("wrong");

        assertThrows(RuntimeException.class, () -> authService.login(dto));
    }
}