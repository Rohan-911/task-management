package com.frontend.userservice.dto;

import java.util.List;

public class UserResponseDTO {

    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;

    public UserResponseDTO(Integer userId, String username, String email, String fullName, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }

    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public List<String> getRoles() { return roles; }
}
