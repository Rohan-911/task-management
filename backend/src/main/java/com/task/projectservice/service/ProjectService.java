package com.task.projectservice.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


import com.task.projectservice.dto.ProjectRequestDto;
import com.task.projectservice.dto.ProjectResponseDto;
import com.task.projectservice.entity.Project;
import com.task.projectservice.repository.ProjectRepository;
import com.task.taskservice.exception.ResourceNotFoundException;
import com.task.user.dto.UserResponseDTO;
import com.task.user.entity.User;
import com.task.user.repository.UserRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    private ProjectResponseDto mapToDto(Project project) {
        return new ProjectResponseDto(
                project.getProjectId(),
                project.getProjectName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getUser() != null ? project.getUser().getUserId() : null,
                project.getUser() != null ? project.getUser().getUsername() : null
        );
    }

    public ProjectResponseDto createProjectForUser(Integer userId, ProjectRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (projectRepository.existsById(dto.getProjectId())) {
            throw new RuntimeException("Project already exists with ID: " + dto.getProjectId());
        }

        Project project = new Project();
        project.setProjectId(dto.getProjectId());
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setUser(user);

        return mapToDto(projectRepository.save(project));
    }

    public ProjectResponseDto getProjectById(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        return mapToDto(project);
    }

    public List<ProjectResponseDto> getProjectsByUserId(Integer userId) {
   	 if (!userRepository.existsById(userId)) {
   	        throw new ResourceNotFoundException("User not found with ID: " + userId);
   	    }

       return projectRepository.findByUser_UserId(userId)
               .stream()
               .map(this::mapToDto)
               .collect(Collectors.toList());
   }

    public List<ProjectResponseDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserOfProject(Integer projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        User user = project.getUser();

        if (user == null) {
            throw new ResourceNotFoundException("No user assigned to project ID: " + projectId);
        }

        List<String> roles = (user.getUserRoles() != null)
                ? user.getUserRoles().stream()
                      .map(ur -> ur.getRole().getRoleName())
                      .distinct()
                      .toList()
                : List.of();

        return new UserResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                roles
        );
    }

    public ProjectResponseDto updateProject(Integer projectId, ProjectRequestDto dto) {

        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (dto.getUserId() == null) {
            throw new RuntimeException("User ID cannot be null");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getUserId()));

        existingProject.setProjectName(dto.getProjectName());
        existingProject.setDescription(dto.getDescription());
        existingProject.setStartDate(dto.getStartDate());
        existingProject.setEndDate(dto.getEndDate());
        existingProject.setUser(user);

        return mapToDto(projectRepository.save(existingProject));
    }

    public ProjectResponseDto changeProjectUser(Integer projectId, Integer userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        project.setUser(user);

        return mapToDto(projectRepository.save(project));
    }

    public void deleteProject(Integer projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        projectRepository.delete(project);
    }


    public boolean projectExists(Integer projectId) {
        return projectRepository.existsById(projectId);
    }


    public long countProjectsByUser(Integer userId) {
        return projectRepository.countByUser_UserId(userId);
    }


}