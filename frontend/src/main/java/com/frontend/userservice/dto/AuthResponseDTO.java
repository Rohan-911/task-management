package com.frontend.userservice.dto;

import java.util.List;

public class AuthResponseDTO {

    private String token;
    private Integer userId;
    private List<String> roles;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, Integer userId, List<String> roles) {
        this.token = token;
        this.userId = userId;
        this.roles = roles;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}
