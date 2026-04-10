package com.task.user.security;

import com.task.user.entity.User;
import com.task.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        // ✅ VERY IMPORTANT (use WITH ROLES)
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<SimpleGrantedAuthority> authorities =
                user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getRoleName())
                        .map(role -> "ROLE_" + role.toUpperCase())
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}