package com.task.projectservice.service;

import java.util.List;

import com.task.projectservice.dto.ProjectRequestDto;
import com.task.projectservice.dto.ProjectResponseDto;
import com.task.user.dto.UserResponseDTO;

public interface ProjectService {
	
	ProjectResponseDto createProjectForUser(Integer userId, ProjectRequestDto dto);

    ProjectResponseDto getProjectById(Integer projectId);

    List<ProjectResponseDto> getProjectsByUserId(Integer userId);

    List<ProjectResponseDto> getAllProjects();

    UserResponseDTO getUserOfProject(Integer projectId);

    ProjectResponseDto updateProject(Integer projectId, ProjectRequestDto dto);

    ProjectResponseDto patchProject(Integer projectId, ProjectRequestDto dto);

    ProjectResponseDto removeUserFromProject(Integer projectId);

    ProjectResponseDto changeProjectUser(Integer projectId, Integer userId);

    void deleteProject(Integer projectId);

    boolean projectExists(Integer projectId);

    long countProjectsByUser(Integer userId);

    List<ProjectResponseDto> getUnassignedProjects();

    ProjectResponseDto getLatestProject(Integer userId);

}
