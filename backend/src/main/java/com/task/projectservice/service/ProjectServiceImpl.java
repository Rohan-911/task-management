package com.task.projectservice.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.task.category.entity.Notification;
import com.task.category.service.NotificationService;
import com.task.exception.AccessDeniedException;
import com.task.exception.BadRequestException;
import com.task.exception.DuplicateResourceException;
import com.task.exception.ResourceNotFoundException;
import com.task.exception.UnauthorizedException;
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

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              UserRepository userRepository,
                              NotificationService notificationService) {
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

    private User getLoggedInUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private boolean hasRole(User user, String roleName) {
        if (user == null || user.getUserRoles() == null) {
            return false;
        }

        return user.getUserRoles().stream()
                .anyMatch(userRole ->
                        userRole.getRole() != null &&
                        userRole.getRole().getRoleName() != null &&
                        userRole.getRole().getRoleName().equalsIgnoreCase(roleName)
                );
    }

    private boolean isAdminOrManager(User user) {
        return hasRole(user, "ADMIN") || hasRole(user, "MANAGER");
    }

    /**
     * Only assigned user can edit project details.
     * If project is unassigned, normal users cannot edit it.
     */
    private void checkProjectEditAccess(Project project, User loggedInUser) {
    	
    	//checking if no user is logged in
        if (loggedInUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }
        
        //checking if project is not assigned any user
        if (project.getUser() == null) {
            throw new BadRequestException("Unassigned projects cannot be modified by normal users");
        }
        
        //making sure that user assigned to project is the same as logged in user
        if (!loggedInUser.getUserId().equals(project.getUser().getUserId())) {
            throw new AccessDeniedException("You are not allowed to modify this project");
        }
    }

    /**
     * Only ADMIN or MANAGER can assign, unassign, or change project owner.
     */
    private void checkAssignmentAccess(User loggedInUser) {
        if (loggedInUser == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        if (!isAdminOrManager(loggedInUser)) {
            throw new AccessDeniedException("Only admin or manager can assign/unassign projects");
        }
    }

    private void createNotification(Integer userId, String message) {
        if (userId == null) return;

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setText(message);

        notificationService.createNotification(notification);
    }

    public ProjectResponseDto createProjectForUser(Integer userId, ProjectRequestDto dto) {
        User loggedInUser = getLoggedInUser();
        
        //check if user id set in project creation request is the same logged user 
        if (!loggedInUser.getUserId().equals(userId)) {
            throw new AccessDeniedException("You cannot create project for another user");
        }

        //check if project id is unique
        if (projectRepository.findById(dto.getProjectId()).isPresent()) {
            throw new DuplicateResourceException("Project id: " + dto.getProjectId() + " already exists");
        }

        Project project = new Project();
        project.setProjectId(dto.getProjectId());
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setUser(loggedInUser);

        Project saved = projectRepository.save(project);

        createNotification(loggedInUser.getUserId(),
                "Project '" + saved.getProjectName() + "' created successfully");

        return mapToDto(saved);
    }

    public ProjectResponseDto getProjectById(Integer projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        return mapToDto(project);
    }

    public List<ProjectResponseDto> getProjectsByUserId(Integer userId) {
    	
    	//checking if user actually exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        //retrieving all projects with userId
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
    
    public List<ProjectResponseDto> getActiveProjects() {
        LocalDate today = LocalDate.now();

        return projectRepository.findByEndDateGreaterThanEqual(today)
                .stream()
                .filter(project -> project.getEndDate() != null)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ProjectResponseDto> getCompletedProjects() {
        LocalDate today = LocalDate.now();

        return projectRepository.findByEndDateLessThan(today)
                .stream()
                .filter(project -> project.getEndDate() != null)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public boolean projectExists(Integer projectId) {
        return projectRepository.existsById(projectId);
    }

    public long countProjectsByUser(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return projectRepository.countByUser_UserId(userId);
    }

    public List<ProjectResponseDto> getUnassignedProjects() {
        List<Project> projects = projectRepository.findByUserIsNull();

        return projects.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ProjectResponseDto getLatestProject(Integer userId) {
        userRepository.findById(userId)
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

    public ProjectResponseDto updateProject(Integer projectId, ProjectRequestDto dto) {
        User loggedInUser = getLoggedInUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        checkProjectEditAccess(project, loggedInUser);

        Integer oldUserId = project.getUser() != null ? project.getUser().getUserId() : null;

        project.setProjectId(projectId);
        project.setProjectName(dto.getProjectName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());

        // owner should not be changed here
        Project updated = projectRepository.save(project);

        createNotification(oldUserId,
                "Project '" + updated.getProjectName() + "' updated");

        return mapToDto(updated);
    }


    public ProjectResponseDto removeUserFromProject(Integer projectId) {
        User loggedInUser = getLoggedInUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        checkAssignmentAccess(loggedInUser);

        if (project.getUser() == null) {
            throw new ResourceNotFoundException("No user assigned");
        }

        Integer oldUserId = project.getUser().getUserId();
        String projectName = project.getProjectName();

        project.setUser(null);
        Project updated = projectRepository.save(project);

        createNotification(oldUserId,
                "You have been removed from project '" + projectName + "'");

        return mapToDto(updated);
    }

    public ProjectResponseDto changeProjectUser(Integer projectId, Integer userId) {
        User loggedInUser = getLoggedInUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        checkAssignmentAccess(loggedInUser);

        User newUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Integer oldUserId = project.getUser() != null ? project.getUser().getUserId() : null;

        project.setUser(newUser);
        Project updated = projectRepository.save(project);

        if (oldUserId != null && !oldUserId.equals(newUser.getUserId())) {
            createNotification(oldUserId,
                    "You have been unassigned from project '" + updated.getProjectName() + "'");
        }

        createNotification(newUser.getUserId(),
                "You have been assigned to project '" + updated.getProjectName() + "'");

        return mapToDto(updated);
    }

    public void deleteProject(Integer projectId) {
        User loggedInUser = getLoggedInUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        checkProjectEditAccess(project, loggedInUser);

        projectRepository.delete(project);
    }
}