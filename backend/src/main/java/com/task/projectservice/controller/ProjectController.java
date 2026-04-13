package com.task.projectservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.task.projectservice.dto.ProjectRequestDto;
import com.task.projectservice.dto.ProjectResponseDto;
import com.task.projectservice.service.ProjectService;
import com.task.user.dto.UserResponseDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/users/{userId}/projects")
    public ResponseEntity<ProjectResponseDto> createProjectForUser(
            @PathVariable Integer userId,
            @Valid @RequestBody ProjectRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProjectForUser(userId, dto));
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable Integer projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/users/{userId}/projects")
    public ResponseEntity<List<ProjectResponseDto>> getProjectsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(projectService.getProjectsByUserId(userId));
    }

    @GetMapping("/projects/{projectId}/user")
    public ResponseEntity<UserResponseDTO> getUserOfProject(@PathVariable Integer projectId) {
        return ResponseEntity.ok(projectService.getUserOfProject(projectId));
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<ProjectResponseDto> updateProject(
            @PathVariable Integer projectId,
            @Valid @RequestBody ProjectRequestDto dto) {

        return ResponseEntity.ok(projectService.updateProject(projectId, dto));
    }

    @PutMapping("/projects/{projectId}/user/{userId}")
    public ResponseEntity<ProjectResponseDto> changeProjectUser(
            @PathVariable Integer projectId,
            @PathVariable Integer userId) {

        return ResponseEntity.ok(projectService.changeProjectUser(projectId, userId));
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Void> deleteProject(@PathVariable Integer projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{projectId}/exists")
    public ResponseEntity<Boolean> projectExists(@PathVariable Integer projectId) {
        return ResponseEntity.ok(projectService.projectExists(projectId));
    }

    @GetMapping("/users/{userId}/projects/count")
    public ResponseEntity<Long> countProjectsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(projectService.countProjectsByUser(userId));
    }

   
}