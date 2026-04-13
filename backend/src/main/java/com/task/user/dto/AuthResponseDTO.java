package com.task.user.dto;

import java.util.List;

public class AuthResponseDTO {

    private String token;
    private Integer userId;
    private List<String> roles;

    public AuthResponseDTO(String token, Integer userId, List<String> roles) {
        this.token = token;
        this.userId = userId;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public Integer getUserId() { return userId; }
    public List<String> getRoles() { return roles; }
}