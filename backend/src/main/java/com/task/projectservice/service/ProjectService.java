package com.task.projectservice.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.task.projectservice.dto.ProjectRequestDto;
import com.task.projectservice.dto.ProjectResponseDto;
import com.task.projectservice.entity.Project;
import com.task.projectservice.repository.ProjectRepository;
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
                project.getUser().getUserId(),
                project.getUser().getUsername() 
                
        );
    }

    public ProjectResponseDto addProject(ProjectRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return mapToDto(project);
    }

    public List<ProjectResponseDto> getProjectsByUserId(Integer userId) {
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

    public ProjectResponseDto updateProject(Integer projectId, ProjectRequestDto dto) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingProject.setProjectName(dto.getProjectName());
        existingProject.setDescription(dto.getDescription());
        existingProject.setStartDate(dto.getStartDate());
        existingProject.setEndDate(dto.getEndDate());
        existingProject.setUser(user);

        return mapToDto(projectRepository.save(existingProject));
    }

    public void deleteProject(Integer projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }
        projectRepository.deleteById(projectId);
    }
}
}