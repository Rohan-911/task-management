package com.task.projectservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.task.exception.DuplicateResourceException;
import com.task.exception.ResourceNotFoundException;
import com.task.projectservice.dto.ProjectRequestDto;
import com.task.projectservice.dto.ProjectResponseDto;
import com.task.projectservice.entity.Project;
import com.task.projectservice.repository.ProjectRepository;

import com.task.user.entity.User;

import com.task.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setUserId(1);
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setFullName("Test User");

        project = new Project();
        project.setProjectId(1);
        project.setProjectName("Test Project");
        project.setDescription("Test Description");
        project.setStartDate(LocalDate.of(2024, 1, 10));
        project.setEndDate(LocalDate.of(2024, 2, 10));
        project.setUser(user);
    }

    @Test
    void createProjectForUser_Success() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectId(2);
        dto.setProjectName("New Project");
        dto.setDescription("New Description");
        dto.setStartDate(LocalDate.of(2024, 3, 1));
        dto.setEndDate(LocalDate.of(2024, 4, 1));

        Project savedProject = new Project();
        savedProject.setProjectId(2);
        savedProject.setProjectName("New Project");
        savedProject.setDescription("New Description");
        savedProject.setStartDate(LocalDate.of(2024, 3, 1));
        savedProject.setEndDate(LocalDate.of(2024, 4, 1));
        savedProject.setUser(user);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.existsById(2)).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponseDto result = projectService.createProjectForUser(1, dto);

        assertNotNull(result);
        assertEquals(2, result.getProjectId());
        assertEquals("New Project", result.getProjectName());
        assertEquals(1, result.getUserId());
    }

    @Test
    void createProjectForUser_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectId(2);

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.createProjectForUser(1, dto));
    }

    @Test
    void createProjectForUser_DuplicateProject() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectId(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.existsById(2)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> projectService.createProjectForUser(1, dto));
    }

    @Test
    void getProjectById_Success() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        ProjectResponseDto result = projectService.getProjectById(1);

        assertEquals("Test Project", result.getProjectName());
        assertEquals(1, result.getUserId());
    }

    @Test
    void getProjectById_NotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getProjectById(1));
    }

    @Test
    void getProjectsByUserId_Success() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.findByUser_UserId(1)).thenReturn(List.of(project));

        List<ProjectResponseDto> result = projectService.getProjectsByUserId(1);

        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getProjectName());
    }

    @Test
    void getProjectsByUserId_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getProjectsByUserId(1));
    }

    @Test
    void getAllProjects_Success() {
        when(projectRepository.findAll()).thenReturn(List.of(project));

        List<ProjectResponseDto> result = projectService.getAllProjects();

        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getProjectName());
    }

    @Test
    void getUserOfProject_Success() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        var result = projectService.getUserOfProject(1);

        assertEquals(1, result.getUserId());
        assertEquals("testUser", result.getUsername());
    }

    @Test
    void getUserOfProject_ProjectNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getUserOfProject(1));
    }

    @Test
    void getUserOfProject_NoUserAssigned() {
        project.setUser(null);
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getUserOfProject(1));
    }

    @Test
    void updateProject_Success_WithUser() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectName("Updated Project");
        dto.setDescription("Updated Description");
        dto.setStartDate(LocalDate.of(2024, 5, 1));
        dto.setEndDate(LocalDate.of(2024, 6, 1));
        dto.setUserId(1);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDto result = projectService.updateProject(1, dto);

        assertEquals("Updated Project", result.getProjectName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(1, result.getUserId());
    }

    @Test
    void updateProject_Success_WithoutUser() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectName("Updated Project");
        dto.setDescription("Updated Description");
        dto.setStartDate(LocalDate.of(2024, 5, 1));
        dto.setEndDate(LocalDate.of(2024, 6, 1));
        dto.setUserId(null);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDto result = projectService.updateProject(1, dto);

        assertEquals("Updated Project", result.getProjectName());
        assertNull(result.getUserId());
    }

    @Test
    void updateProject_ProjectNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.updateProject(1, new ProjectRequestDto()));
    }

    @Test
    void updateProject_UserNotFound() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setUserId(99);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.updateProject(1, dto));
    }

    @Test
    void patchProject_Success() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectName("Patched Project");

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDto result = projectService.patchProject(1, dto);

        assertEquals("Patched Project", result.getProjectName());
        assertEquals("Test Description", result.getDescription());
    }

    @Test
    void patchProject_WithUser_Success() {
        User newUser = new User();
        newUser.setUserId(2);
        newUser.setUsername("newUser");

        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setUserId(2);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(2)).thenReturn(Optional.of(newUser));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDto result = projectService.patchProject(1, dto);

        assertEquals(2, result.getUserId());
        assertEquals("newUser", result.getUserName());
    }

    @Test
    void patchProject_ProjectNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.patchProject(1, new ProjectRequestDto()));
    }

    @Test
    void patchProject_UserNotFound() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setUserId(99);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.patchProject(1, dto));
    }

    @Test
    void removeUserFromProject_Success() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDto result = projectService.removeUserFromProject(1);

        assertNull(result.getUserId());
        assertNull(result.getUserName());
    }

    @Test
    void removeUserFromProject_ProjectNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.removeUserFromProject(1));
    }

    @Test
    void removeUserFromProject_NoUser() {
        project.setUser(null);
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.removeUserFromProject(1));
    }

    @Test
    void changeProjectUser_Success() {
        User newUser = new User();
        newUser.setUserId(2);
        newUser.setUsername("newUser");

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(2)).thenReturn(Optional.of(newUser));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDto result = projectService.changeProjectUser(1, 2);

        assertEquals(2, result.getUserId());
        assertEquals("newUser", result.getUserName());
    }

    @Test
    void changeProjectUser_ProjectNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.changeProjectUser(1, 2));
    }

    @Test
    void changeProjectUser_UserNotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.changeProjectUser(1, 2));
    }

    @Test
    void deleteProject_Success() {
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));

        projectService.deleteProject(1);

        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    void deleteProject_NotFound() {
        when(projectRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.deleteProject(1));
    }

    @Test
    void projectExists_True() {
        when(projectRepository.existsById(1)).thenReturn(true);

        assertTrue(projectService.projectExists(1));
    }

    @Test
    void countProjectsByUser_Success() {
        when(projectRepository.countByUser_UserId(1)).thenReturn(3L);

        assertEquals(3L, projectService.countProjectsByUser(1));
    }

    @Test
    void getUnassignedProjects_Success() {
        Project unassigned = new Project();
        unassigned.setProjectId(2);
        unassigned.setProjectName("Unassigned");
        unassigned.setStartDate(LocalDate.of(2024, 7, 1));

        when(projectRepository.findByUserIsNull()).thenReturn(List.of(unassigned));

        List<ProjectResponseDto> result = projectService.getUnassignedProjects();

        assertEquals(1, result.size());
        assertEquals("Unassigned", result.get(0).getProjectName());
        assertNull(result.get(0).getUserId());
    }

    @Test
    void getUnassignedProjects_Empty() {
        when(projectRepository.findByUserIsNull()).thenReturn(Collections.emptyList());

        List<ProjectResponseDto> result = projectService.getUnassignedProjects();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getLatestProject_Success() {
        Project older = new Project();
        older.setProjectId(1);
        older.setProjectName("Older");
        older.setStartDate(LocalDate.of(2024, 1, 1));
        older.setUser(user);

        Project latest = new Project();
        latest.setProjectId(2);
        latest.setProjectName("Latest");
        latest.setStartDate(LocalDate.of(2024, 5, 1));
        latest.setUser(user);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.findByUser_UserId(1)).thenReturn(List.of(older, latest));

        ProjectResponseDto result = projectService.getLatestProject(1);

        assertNotNull(result);
        assertEquals(2, result.getProjectId());
        assertEquals("Latest", result.getProjectName());
    }

    @Test
    void getLatestProject_UserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getLatestProject(1));
    }

    @Test
    void getLatestProject_NoProjects() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(projectRepository.findByUser_UserId(1)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class,
                () -> projectService.getLatestProject(1));
    }
}

