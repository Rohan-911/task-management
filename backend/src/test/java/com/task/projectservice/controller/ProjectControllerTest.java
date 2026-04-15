package com.task.projectservice.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.task.projectservice.dto.ProjectRequestDto;
import com.task.projectservice.dto.ProjectResponseDto;
import com.task.projectservice.service.ProjectService;
import com.task.user.dto.UserResponseDTO;

class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setValidator(validator)
                .build();
    }

    @Test
    void createProjectForUser_Success() throws Exception {

        ProjectResponseDto response = new ProjectResponseDto();
        response.setProjectId(1);
        response.setProjectName("Test Project");

        when(projectService.createProjectForUser(eq(1), any(ProjectRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/users/1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectId": 1,
                                  "projectName": "Test Project",
                                  "startDate": "2026-04-16"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId").value(1));
    }

    @Test
    void getProjectById_Success() throws Exception {
        ProjectResponseDto response = new ProjectResponseDto();
        response.setProjectId(1);

        when(projectService.getProjectById(1)).thenReturn(response);

        mockMvc.perform(get("/api/v1/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1));
    }

    @Test
    void getAllProjects_Success() throws Exception {
        when(projectService.getAllProjects())
                .thenReturn(List.of(new ProjectResponseDto()));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectsByUserId_Success() throws Exception {
        when(projectService.getProjectsByUserId(1))
                .thenReturn(List.of(new ProjectResponseDto()));

        mockMvc.perform(get("/api/v1/users/1/projects"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserOfProject_Success() throws Exception {
        UserResponseDTO user = new UserResponseDTO(
                1,
                "testuser",
                "test@example.com",
                "Test User",
                List.of("USER"));

        when(projectService.getUserOfProject(1)).thenReturn(user);

        mockMvc.perform(get("/api/v1/projects/1/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void updateProject_Success() throws Exception {

        ProjectResponseDto response = new ProjectResponseDto();
        response.setProjectName("Updated");

        when(projectService.updateProject(eq(1), any(ProjectRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "projectName": "Updated",
                                  "startDate": "2026-04-16"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("Updated"));
    }

    @Test
    void patchProject_Success() throws Exception {
        ProjectResponseDto response = new ProjectResponseDto();
        response.setProjectName("Patched");

        when(projectService.patchProject(eq(1), any(ProjectRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/api/v1/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void removeUserFromProject_Success() throws Exception {
        when(projectService.removeUserFromProject(1))
                .thenReturn(new ProjectResponseDto());

        mockMvc.perform(patch("/api/v1/projects/1/user/remove"))
                .andExpect(status().isOk());
    }

    @Test
    void changeProjectUser_Success() throws Exception {
        when(projectService.changeProjectUser(1, 2))
                .thenReturn(new ProjectResponseDto());

        mockMvc.perform(patch("/api/v1/projects/1/user/2"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProject_Success() throws Exception {
        doNothing().when(projectService).deleteProject(1);

        mockMvc.perform(delete("/api/v1/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void projectExists_Success() throws Exception {
        when(projectService.projectExists(1)).thenReturn(true);

        mockMvc.perform(get("/api/v1/projects/1/exists"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void countProjectsByUser_Success() throws Exception {
        when(projectService.countProjectsByUser(1)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/users/1/projects/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void getUnassignedProjects_Success() throws Exception {
        when(projectService.getUnassignedProjects())
                .thenReturn(List.of(new ProjectResponseDto()));

        mockMvc.perform(get("/api/v1/projects/unassigned"))
                .andExpect(status().isOk());
    }

    @Test
    void getLatestProject_Success() throws Exception {
        when(projectService.getLatestProject(1))
                .thenReturn(new ProjectResponseDto());

        mockMvc.perform(get("/api/v1/users/1/projects/latest"))
                .andExpect(status().isOk());
    }
}