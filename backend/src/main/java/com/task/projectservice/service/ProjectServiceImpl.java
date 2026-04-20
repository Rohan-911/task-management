package com.task.projectservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.task.category.entity.Notification;
import com.task.category.service.NotificationService;
import com.task.exception.DuplicateResourceException;
import com.task.exception.ResourceNotFoundException;
import com.task.projectservice.dto.ProjectRequestDto;
import com.task.projectservice.dto.ProjectResponseDto;
import com.task.projectservice.entity.Project;
import com.task.projectservice.repository.ProjectRepository;
import com.task.user.dto.UserResponseDTO;
import com.task.user.entity.User;
import com.task.user.repository.UserRepository;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    
    public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository,NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
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
    
    private void createNotification(Integer userId, String message) {
        if (userId == null) return;

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setText(message);

        notificationService.createNotification(notification);
    }
    

    public ProjectResponseDto createProjectForUser(Integer userId, ProjectRequestDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (projectRepository.findById(dto.getProjectId()).isPresent()) {
            throw new DuplicateResourceException("Project id: " + dto.getProjectId() + " already exists");
        }

        Project project = new Project();

        project.setProjectId(dto.getProjectId());
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setUser(user);
        
        Project saved = projectRepository.save(project);
        
        createNotification(userId, "Project '" + saved.getProjectName() + "' created successfully");


        return mapToDto(saved);
    }

    public ProjectResponseDto getProjectById(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        return mapToDto(project);
    }

    public List<ProjectResponseDto> getProjectsByUserId(Integer userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

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

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        Integer oldUserId = project.getUser() != null ? project.getUser().getUserId() : null;

        User user = null;
        if (dto.getUserId() != null) {
            user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getUserId()));
        }
        
        project.setProjectId(projectId);
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setUser(user);

        Project updated = projectRepository.save(project);

        createNotification(
                user != null ? user.getUserId() : oldUserId,
                "Project '" + updated.getProjectName() + "' updated"
        );

        return mapToDto(updated);
    }

    public ProjectResponseDto patchProject(Integer projectId, ProjectRequestDto dto) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        Integer oldUserId = project.getUser() != null ? project.getUser().getUserId() : null;

        if (dto.getProjectName() != null) {
            project.setProjectName(dto.getProjectName());
        }

        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }

        if (dto.getStartDate() != null) {
            project.setStartDate(dto.getStartDate());
        }

        if (dto.getEndDate() != null) {
            project.setEndDate(dto.getEndDate());
        }

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + dto.getUserId()));
            project.setUser(user);
        }

        Project updated = projectRepository.save(project);

        Integer newUserId = updated.getUser() != null ? updated.getUser().getUserId() : oldUserId;

        createNotification(newUserId, "Project '" + updated.getProjectName() + "' modified");

        return mapToDto(updated);
    }
    
    public ProjectResponseDto removeUserFromProject(Integer projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (project.getUser() == null) {
            throw new ResourceNotFoundException("No user assigned to this project");
        }

        Integer userId = project.getUser().getUserId();
        String projectName = project.getProjectName();

        project.setUser(null);
        projectRepository.save(project);

        createNotification(userId, "You have been removed from project '" + projectName + "'");

        return mapToDto(project);
    }

    public ProjectResponseDto changeProjectUser(Integer projectId, Integer userId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        project.setUser(user);
        Project updated = projectRepository.save(project);

        createNotification(userId, "You have been assigned to project '" + updated.getProjectName() + "'");

        return mapToDto(updated);
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

    public List<ProjectResponseDto> getUnassignedProjects() {

        List<Project> projects = projectRepository.findByUserIsNull();

        return projects.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProjectResponseDto getLatestProject(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Project> projects = projectRepository.findByUser_UserId(userId);

        if (projects.isEmpty()) {
            throw new ResourceNotFoundException("No projects found for this user");
        }

        Project latestProject = projects.stream()
                .max(Comparator.comparing(Project::getStartDate))
                .orElseThrow(() -> new ResourceNotFoundException("No projects found"));

        return mapToDto(latestProject);
    }
}