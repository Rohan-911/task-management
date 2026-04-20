package com.task.frontend.service;

import com.task.frontend.dto.RestResponsePage;
import com.task.frontend.dto.UserRequestDTO;
import com.task.frontend.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FrontendUserService {

    private final RestTemplate restTemplate;

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    public FrontendUserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Page<UserResponseDTO> getAllUsers(int page, int size) {
        String url = backendUrl + "/api/users?page=" + page + "&size=" + size;
        ResponseEntity<RestResponsePage<UserResponseDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<RestResponsePage<UserResponseDTO>>() {});
        return response.getBody();
    }

    public List<String> getAllRoles() {
        String url = backendUrl + "/api/roles";
        ResponseEntity<List<String>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {});
        return response.getBody();
    }

    public void createUser(UserRequestDTO dto) {
        String url = backendUrl + "/api/users";
        restTemplate.postForEntity(url, dto, Void.class);
    }

    public UserResponseDTO getUserById(Integer id) {
        String url = backendUrl + "/api/users/" + id;
        return restTemplate.getForObject(url, UserResponseDTO.class);
    }

    public void updateUser(Integer id, UserRequestDTO dto) {
        String url = backendUrl + "/api/users/" + id;
        restTemplate.put(url, dto);
    }

    public void deleteUser(Integer id) {
        String url = backendUrl + "/api/users/" + id;
        restTemplate.delete(url);
    }

    public Page<UserResponseDTO> searchUsers(String username, int page, int size) {
        String url = backendUrl + "/api/users/search?username=" + username + "&page=" + page + "&size=" + size;
        ResponseEntity<RestResponsePage<UserResponseDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<RestResponsePage<UserResponseDTO>>() {});
        return response.getBody();
    }

    public Page<UserResponseDTO> getUsersByRole(String role, int page, int size) {
        String url = backendUrl + "/api/users/role?role=" + role + "&page=" + page + "&size=" + size;
        ResponseEntity<RestResponsePage<UserResponseDTO>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<RestResponsePage<UserResponseDTO>>() {});
        return response.getBody();
    }

    public List<String> getUserRoles(Integer id) {
        String url = backendUrl + "/api/users/" + id + "/roles";
        ResponseEntity<List<String>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>() {});
        return response.getBody();
    }

    public void assignRolesToUser(Integer id, List<String> roles) {
        String url = backendUrl + "/api/users/" + id + "/roles";
        restTemplate.postForEntity(url, roles, Void.class);
    }

    public UserResponseDTO getCurrentUser() {
        String url = backendUrl + "/api/users/me";
        return restTemplate.getForObject(url, UserResponseDTO.class);
    }

    public void updateCurrentUser(UserRequestDTO dto) {
        String url = backendUrl + "/api/users/me";
        restTemplate.put(url, dto);
    }
}
