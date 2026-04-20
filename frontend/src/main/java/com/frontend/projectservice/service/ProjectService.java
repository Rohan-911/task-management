package com.frontend.projectservice.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.frontend.projectservice.dto.ProjectRequestDto;
import com.frontend.projectservice.dto.ProjectResponseDto;
import com.frontend.projectservice.dto.UserResponseDto;

@Service
public class ProjectService {

    public List<ProjectResponseDto> getAllProjects(String token) {
        return projectClient(token).get()
                .uri("/projects")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProjectResponseDto>>() {
                });
    }

    public ProjectResponseDto getProjectById(Integer projectId, String token) {
        return projectClient(token).get()
                .uri("/projects/{projectId}", projectId)
                .retrieve()
                .body(ProjectResponseDto.class);
    }

    public ProjectResponseDto createProject(Integer userId, ProjectRequestDto dto, String token) {
        return projectClient(token).post()
                .uri("/users/{userId}/projects", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(ProjectResponseDto.class);
    }

    public ProjectResponseDto updateProject(Integer projectId, ProjectRequestDto dto, String token) {
        return projectClient(token).put()
                .uri("/projects/{projectId}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .retrieve()
                .body(ProjectResponseDto.class);
    }

    public List<ProjectResponseDto> getProjectsByUser(Integer userId, String token) {
        return projectClient(token).get()
                .uri("/users/{userId}/projects", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProjectResponseDto>>() {
                });
    }

    public List<ProjectResponseDto> getActiveProjects(String token) {
        return projectClient(token).get()
                .uri("/projects/active")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProjectResponseDto>>() {
                });
    }

    public List<ProjectResponseDto> getCompletedProjects(String token) {
        return projectClient(token).get()
                .uri("/projects/completed")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProjectResponseDto>>() {
                });
    }

    public Long getProjectCountByUser(Integer userId, String token) {
        return projectClient(token).get()
                .uri("/users/{userId}/projects/count", userId)
                .retrieve()
                .body(Long.class);
    }

    public ProjectResponseDto getLatestProjectByUser(Integer userId, String token) {
        return projectClient(token).get()
                .uri("/users/{userId}/projects/latest", userId)
                .retrieve()
                .body(ProjectResponseDto.class);
    }

    public Boolean projectExists(Integer projectId, String token) {
        return projectClient(token).get()
                .uri("/projects/{projectId}/exists", projectId)
                .retrieve()
                .body(Boolean.class);
    }

    public List<ProjectResponseDto> getUnassignedProjects(String token) {
        return projectClient(token).get()
                .uri("/projects/unassigned")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProjectResponseDto>>() {
                });
    }

    public UserResponseDto getUserOfProject(Integer projectId, String token) {
        return projectClient(token).get()
                .uri("/projects/{projectId}/user", projectId)
                .retrieve()
                .body(UserResponseDto.class);
    }

    public ProjectResponseDto changeProjectUser(Integer projectId, Integer userId, String token) {
        return projectClient(token).patch()
                .uri("/projects/{projectId}/user/{userId}", projectId, userId)
                .retrieve()
                .body(ProjectResponseDto.class);
    }

    public ProjectResponseDto removeUserFromProject(Integer projectId, String token) {
        return projectClient(token).patch()
                .uri("/projects/{projectId}/user/remove", projectId)
                .retrieve()
                .body(ProjectResponseDto.class);
    }

    public UserResponseDto getUserById(Integer userId, String token) {
        return userClient(token).get()
                .uri("/users/{id}", userId)
                .retrieve()
                .body(UserResponseDto.class);
    }

    private RestClient projectClient(String token) {
        return RestClient.builder()
                .baseUrl("http://localhost:8080/api/v1")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    private RestClient userClient(String token) {
        return RestClient.builder()
                .baseUrl("http://localhost:8080/api")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }
}
