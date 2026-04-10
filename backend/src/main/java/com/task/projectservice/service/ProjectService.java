package com.task.projectservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.task.projectservice.dto.ProjectRequestDto;
import com.task.projectservice.entity.Project;
import com.task.projectservice.repository.ProjectRepository;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Project addProject(ProjectRequestDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = new Project();
        project.setProjectId(dto.getProjectId());
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setUser(user);

        return projectRepository.save(project);
    }

    public Project getProjectById(Integer projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public List<Project> getProjectsByUserId(Integer userId) {
        return projectRepository.findByUserUserId(userId);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project updateProject(Integer projectId, ProjectRequestDto dto) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        existingProject.setProjectName(dto.getProjectName());
        existingProject.setDescription(dto.getDescription());
        existingProject.setStartDate(dto.getStartDate());
        existingProject.setEndDate(dto.getEndDate());
        existingProject.setUser(user);

        return projectRepository.save(existingProject);
    }

    public void deleteProject(Integer projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }
        projectRepository.deleteById(projectId);
    }
}