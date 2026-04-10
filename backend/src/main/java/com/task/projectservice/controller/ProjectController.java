package com.task.projectservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.task.projectservice.dto.ProjectRequestDto;
import com.task.projectservice.dto.ProjectResponseDto;
import com.task.projectservice.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponseDto addProject(@Valid @RequestBody ProjectRequestDto dto) {
        return projectService.addProject(dto);
    }

    @GetMapping("/{id}")
    public ProjectResponseDto getProjectById(@PathVariable Integer id) {
        return projectService.getProjectById(id);
    }

    @GetMapping("/user/{userId}")
    public List<ProjectResponseDto> getProjectsByUserId(@PathVariable Integer userId) {
        return projectService.getProjectsByUserId(userId);
    }

    @GetMapping
    public List<ProjectResponseDto> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PutMapping("/{id}")
    public ProjectResponseDto updateProject(@PathVariable Integer id,
                                            @Valid @RequestBody ProjectRequestDto dto) {
        return projectService.updateProject(id, dto);
    }
    
}