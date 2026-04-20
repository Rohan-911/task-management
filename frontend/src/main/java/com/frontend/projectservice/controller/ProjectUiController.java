package com.frontend.projectservice.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.frontend.projectservice.dto.ChangeUserRequest;
import com.frontend.projectservice.dto.DeleteProjectRequest;
import com.frontend.projectservice.dto.NotificationResponseDto;
import com.frontend.projectservice.dto.ProjectRequestDto;
import com.frontend.projectservice.dto.ProjectResponseDto;
import com.frontend.projectservice.dto.UserProjectsSearchRequest;
import com.frontend.projectservice.dto.UserResponseDto;
import com.frontend.projectservice.service.FrontendNotificationService;
import com.frontend.projectservice.service.ProjectService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui")
public class ProjectUiController {

    private static final int PAGE_SIZE = 10;
    private static final Pattern JSON_FIELD_PATTERN = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
    private static final String SESSION_TOKEN = "JWT_TOKEN";
    private static final String SESSION_USERNAME = "USERNAME";
    private static final String LEGACY_SESSION_TOKEN = "jwtToken";
    private static final String LEGACY_SESSION_USERNAME = "username";

    private final ProjectService projectService;
    private final FrontendNotificationService frontendNotificationService;

    public ProjectUiController(ProjectService projectService,
                               FrontendNotificationService frontendNotificationService) {
        this.projectService = projectService;
        this.frontendNotificationService = frontendNotificationService;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping({"", "/", "/projects/dashboard"})
    public String showDashboard(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("username", getUsername(session));
        return "project-service/project-api-page";
    }

    @PostMapping("/projects/open")
    public String openProjectById(@ModelAttribute DeleteProjectRequest deleteProjectRequest,
                                  Model model,
                                  HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(deleteProjectRequest.getProjectId())) {
            return showSimpleError(model, "GET", "Project ID must be a positive number.", "400 Bad Request");
        }

        return "redirect:/ui/project/" + deleteProjectRequest.getProjectId();
    }

    @PostMapping("/projects/user/open")
    public String openProjectUser(@ModelAttribute DeleteProjectRequest deleteProjectRequest,
                                  Model model,
                                  HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(deleteProjectRequest.getProjectId())) {
            prepareProjectUserSearch(model);
            return showSimpleError(model, "GET", "Project ID must be a positive number.", "400 Bad Request");
        }

        return "redirect:/ui/projects/" + deleteProjectRequest.getProjectId() + "/user";
    }

    @PostMapping("/users/projects/open")
    public String openProjectsByUser(@ModelAttribute UserProjectsSearchRequest userProjectsSearchRequest,
                                     Model model,
                                     HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(userProjectsSearchRequest.getUserId())) {
            return showSimpleError(model, "GET", "User ID must be a positive number.", "400 Bad Request");
        }

        return "redirect:/ui/users/" + userProjectsSearchRequest.getUserId() + "/projects";
    }

    @PostMapping("/users/projects/latest")
    public String openLatestProject(@ModelAttribute UserProjectsSearchRequest userProjectsSearchRequest,
                                    Model model,
                                    HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(userProjectsSearchRequest.getUserId())) {
            return showSimpleError(model, "GET", "User ID must be a positive number.", "400 Bad Request");
        }

        return "redirect:/ui/users/" + userProjectsSearchRequest.getUserId() + "/projects/latest";
    }

    @GetMapping("/projects/count")
    public String countProjectsByUserPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        prepareSearchPageDefaults(model);
        prepareCountSearch(model);
        return "project-service/result";
    }

    @GetMapping("/projects/exists")
    public String projectExistsPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        prepareSearchPageDefaults(model);
        prepareProjectExistsSearch(model);
        return "project-service/result";
    }

    @GetMapping("/projects/user")
    public String projectUserPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        prepareSearchPageDefaults(model);
        prepareProjectUserSearch(model);
        return "project-service/result";
    }

    @PostMapping("/projects/count")
    public String openCountProjectsByUser(@ModelAttribute UserProjectsSearchRequest userProjectsSearchRequest,
                                          Model model,
                                          HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(userProjectsSearchRequest.getUserId())) {
            return showSimpleError(model, "GET", "User ID must be a positive number.", "400 Bad Request");
        }

        return "redirect:/ui/users/" + userProjectsSearchRequest.getUserId() + "/projects/count";
    }

    @PostMapping("/projects/exists")
    public String openProjectExists(@ModelAttribute DeleteProjectRequest deleteProjectRequest,
                                    Model model,
                                    HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(deleteProjectRequest.getProjectId())) {
            return showSimpleError(model, "GET", "Project ID must be a positive number.", "400 Bad Request");
        }

        return "redirect:/ui/projects/exists/" + deleteProjectRequest.getProjectId();
    }

    @GetMapping("/notifications")
    public String notificationsPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        prepareNotificationsPage(model, List.of(), false, null);
        return "project-service/notifications";
    }

    @PostMapping("/notifications")
    public String openNotificationsByUser(@ModelAttribute UserProjectsSearchRequest userProjectsSearchRequest,
                                          Model model,
                                          HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(userProjectsSearchRequest.getUserId())) {
            prepareNotificationsPage(model, List.of(), false, "User ID must be a positive number.");
            return "project-service/notifications";
        }

        return "redirect:/ui/notifications/user/" + userProjectsSearchRequest.getUserId();
    }

    @GetMapping("/notifications/user/{userId}")
    public String getNotificationsByUser(@PathVariable String userId, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedUserId = parseId(userId, "User ID");
            List<NotificationResponseDto> notifications = frontendNotificationService
                    .getNotificationsByUser(parsedUserId, getToken(session));
            prepareNotificationsPage(model, notifications, true, null);
            return "project-service/notifications";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "GET", "Unable to load notifications.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            prepareNotificationsPage(model, List.of(), false, ex.getMessage());
            return "project-service/notifications";
        } catch (Exception ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping("/projects/all")
    public String showAllProjects(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            List<ProjectResponseDto> projects = projectService.getAllProjects(getToken(session));
            return buildProjectListPage(model, projects, page, "/ui/projects/all", "All Projects");
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "GET", "Unable to load projects.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (Exception ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping("/projects/active")
    public String getActiveProjects(@RequestParam(defaultValue = "1") int page, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            List<ProjectResponseDto> projects = projectService.getActiveProjects(getToken(session));
            return buildProjectListPage(model, projects, page, "/ui/projects/active", "Active Projects");
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "GET", "Unable to load active projects.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (Exception ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping("/projects/completed")
    public String getCompletedProjects(@RequestParam(defaultValue = "1") int page,
                                       Model model,
                                       HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            List<ProjectResponseDto> projects = projectService.getCompletedProjects(getToken(session));
            return buildProjectListPage(model, projects, page, "/ui/projects/completed", "Completed Projects");
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "GET", "Unable to load completed projects.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (Exception ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping({"/projects/create", "/users/{userId}/projects/create"})
    public String createProjectForUserForm(@PathVariable(required = false) String userId,
                                           Model model,
                                           HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        if (userId != null && !userId.isBlank()) {
            try {
                projectRequestDto.setUserId(parseId(userId, "User ID"));
            } catch (IllegalArgumentException ex) {
                return showSimpleError(model, "POST", ex.getMessage(), "400 Bad Request");
            }
        }

        model.addAttribute("projectRequestDto", projectRequestDto);
        return "project-service/create-project";
    }

    @PostMapping({"/projects/create", "/users/{userId}/projects/create"})
    public String createProjectForUser(@PathVariable(required = false) String userId,
                                       @Valid @ModelAttribute("projectRequestDto") ProjectRequestDto projectRequestDto,
                                       BindingResult bindingResult,
                                       Model model,
                                       HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(projectRequestDto.getUserId()) && userId != null && !userId.isBlank()) {
            try {
                projectRequestDto.setUserId(parseId(userId, "User ID"));
            } catch (IllegalArgumentException ex) {
                return showSimpleError(model, "POST", ex.getMessage(), "400 Bad Request");
            }
        }

        if (projectRequestDto.getUserId() == null) {
            bindingResult.rejectValue("userId", "userId.required", "User ID is required.");
        } else if (isInvalidId(projectRequestDto.getUserId())) {
            bindingResult.rejectValue("userId", "userId.invalid", "User ID must be a positive number.");
        }

        if (projectRequestDto.getProjectId() == null) {
            bindingResult.rejectValue("projectId", "projectId.required", "Project ID is required.");
        } else if (isInvalidId(projectRequestDto.getProjectId())) {
            bindingResult.rejectValue("projectId", "projectId.invalid", "Project ID must be a positive number.");
        }

        validateDateRange(projectRequestDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return showValidationError(model, "POST", bindingResult);
        }

        try {
            ProjectResponseDto createdProject = projectService.createProject(projectRequestDto.getUserId(),
                    projectRequestDto, getToken(session));
            prepareResultPage(model, true, "POST", "Project created successfully.", null, createdProject, null, null);
            return "project-service/result";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            if (hasValidationErrors(ex)) {
                return showBackendValidationError(model, "POST", ex);
            }
            return showResultError(model, "POST", "Project creation failed.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "POST", "Backend service is not reachable.", "503 Service Unavailable");
        } catch (Exception ex) {
            return showSimpleError(model, "POST", ex.getMessage(), "500 Internal Server Error");
        }
    }

    @GetMapping("/project")
    public String getProjectByIdPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("lookupType", "project");
        return "project-service/project-details";
    }

    @GetMapping("/projects/latest")
    public String getLatestProjectPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("lookupType", "latestProject");
        return "project-service/project-details";
    }

    @GetMapping("/users/projects")
    public String getProjectsByUserPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        return buildProjectListPage(model, List.of(), 1, "/ui/users/projects", "Projects By User", false);
    }

    @GetMapping({"/project/{projectId}", "/projects/{projectId}"})
    public String showProjectDetails(@PathVariable String projectId, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedProjectId = parseId(projectId, "Project ID");
            ProjectResponseDto project = projectService.getProjectById(parsedProjectId, getToken(session));
            model.addAttribute("project", project);
            model.addAttribute("lookupType", "project");
            return "project-service/project-details";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "GET", "Unable to load project details.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        } catch (Exception ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping({"/projects/{userId}", "/users/{userId}/projects"})
    public String getProjectsByUserId(@PathVariable String userId,
                                      @RequestParam(defaultValue = "1") int page,
                                      Model model,
                                      HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedUserId = parseId(userId, "User ID");
            List<ProjectResponseDto> projects = projectService.getProjectsByUser(parsedUserId, getToken(session));
            return buildProjectListPage(model, projects, page, "/ui/projects/" + parsedUserId,
                    "Projects For User " + parsedUserId, true);
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "GET", "Unable to load projects for the user.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        } catch (Exception ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping("/projects/unassigned")
    public String showUnassignedProjects(@RequestParam(defaultValue = "1") int page,
                                         Model model,
                                         HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            List<ProjectResponseDto> projects = projectService.getUnassignedProjects(getToken(session));
            return buildProjectListPage(model, projects, page, "/ui/projects/unassigned", "Unassigned Projects");
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "GET", "Unable to load unassigned projects.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (Exception ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping({"/projects/latest/{userId}", "/users/{userId}/projects/latest"})
    public String showLatestProject(@PathVariable String userId, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedUserId = parseId(userId, "User ID");
            ProjectResponseDto project = projectService.getLatestProjectByUser(parsedUserId, getToken(session));
            model.addAttribute("project", project);
            model.addAttribute("lookupType", "latestProject");
            return "project-service/project-details";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "GET", "Unable to load latest project.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        } catch (Exception ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping("/projects/{projectId}/user")
    public String showUserOfProject(@PathVariable String projectId, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedProjectId = parseId(projectId, "Project ID");
            UserResponseDto user = projectService.getUserOfProject(parsedProjectId, getToken(session));
            prepareResultPage(model, true, "GET", "User details fetched successfully.", null, null, user, null);
            return "project-service/result";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "GET", "Unable to load the project user.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        } catch (Exception ex) {
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping("/projects/exists/{projectId}")
    public String projectExists(@PathVariable String projectId, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedProjectId = parseId(projectId, "Project ID");
            Boolean exists = projectService.projectExists(parsedProjectId, getToken(session));
            prepareResultPage(model, true, "GET", "", null, null, null,
                    "Exists: " + Boolean.TRUE.equals(exists));
            prepareProjectExistsSearch(model);
            return "project-service/result";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            prepareProjectExistsSearch(model);
            return showResultError(model, "GET", "Unable to check project existence.", ex);
        } catch (RestClientException ex) {
            prepareProjectExistsSearch(model);
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            prepareProjectExistsSearch(model);
            return showSimpleError(model, "GET", ex.getMessage());
        } catch (Exception ex) {
            prepareProjectExistsSearch(model);
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping({"/projects/count/{userId}", "/users/{userId}/projects/count"})
    public String countProjectsByUser(@PathVariable String userId, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedUserId = parseId(userId, "User ID");
            Long count = projectService.getProjectCountByUser(parsedUserId, getToken(session));
            UserResponseDto user = getUserById(parsedUserId, session);
            ensureUsernameVisible(user, parsedUserId, session);
            String countMessage = "Project Count: " + count;
            if (hasVisibleUsername(user)) {
                countMessage = "Username: " + user.getUsername() + " | " + countMessage;
            }

            prepareResultPage(model, true, "GET", "Project count loaded successfully.", null, null, user,
                    countMessage);
            prepareCountSearch(model);
            return "project-service/result";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            prepareCountSearch(model);
            return showResultError(model, "GET", "Unable to count projects for the user.", ex);
        } catch (RestClientException ex) {
            prepareCountSearch(model);
            return showSimpleError(model, "GET", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            prepareCountSearch(model);
            return showSimpleError(model, "GET", ex.getMessage());
        } catch (Exception ex) {
            prepareCountSearch(model);
            return showSimpleError(model, "GET", ex.getMessage());
        }
    }

    @GetMapping("/projects/edit")
    public String updateProjectLookupPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("deleteProjectRequest", new DeleteProjectRequest());
        return "project-service/edit-project-search";
    }

    @PostMapping("/projects/edit")
    public String openUpdateProjectForm(@ModelAttribute DeleteProjectRequest deleteProjectRequest,
                                        Model model,
                                        HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(deleteProjectRequest.getProjectId())) {
            model.addAttribute("deleteProjectRequest", deleteProjectRequest);
            return showSimpleError(model, "PUT", "Project ID must be a positive number.", "400 Bad Request");
        }

        return "redirect:/ui/projects/edit/" + deleteProjectRequest.getProjectId();
    }

    @GetMapping({"/projects/edit/{projectId}", "/projects/{projectId}/edit"})
    public String updateProjectForm(@PathVariable String projectId, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedProjectId = parseId(projectId, "Project ID");
            ProjectResponseDto project = projectService.getProjectById(parsedProjectId, getToken(session));
            ProjectRequestDto projectRequestDto = new ProjectRequestDto();
            projectRequestDto.setProjectId(project.getProjectId());
            projectRequestDto.setProjectName(project.getProjectName());
            projectRequestDto.setDescription(project.getDescription());
            projectRequestDto.setStartDate(project.getStartDate());
            projectRequestDto.setEndDate(project.getEndDate());
            projectRequestDto.setUserId(project.getUserId());

            model.addAttribute("projectId", parsedProjectId);
            model.addAttribute("projectRequestDto", projectRequestDto);
            return "project-service/edit-project";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "PUT", "Unable to load project for editing.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "PUT", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            return showSimpleError(model, "PUT", ex.getMessage());
        } catch (Exception ex) {
            return showSimpleError(model, "PUT", ex.getMessage());
        }
    }

    @PostMapping({"/projects/edit/{projectId}", "/projects/{projectId}/edit"})
    public String updateProject(@PathVariable String projectId,
                                @Valid @ModelAttribute("projectRequestDto") ProjectRequestDto projectRequestDto,
                                BindingResult bindingResult,
                                Model model,
                                HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        Integer parsedProjectId;
        try {
            parsedProjectId = parseId(projectId, "Project ID");
        } catch (IllegalArgumentException ex) {
            return showSimpleError(model, "PUT", ex.getMessage());
        }

        validateDateRange(projectRequestDto, bindingResult);

        if (bindingResult.hasErrors()) {
            return showValidationError(model, "PUT", bindingResult);
        }

        try {
            ProjectResponseDto updatedProject = projectService.updateProject(parsedProjectId, projectRequestDto,
                    getToken(session));
            prepareResultPage(model, true, "PUT", "Project updated successfully.", null, updatedProject, null, null);
            return "project-service/result";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            if (hasValidationErrors(ex)) {
                return showBackendValidationError(model, "PUT", ex);
            }
            return showResultError(model, "PUT", "Project update failed.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "PUT", "Backend service is not reachable.", "503 Service Unavailable");
        } catch (Exception ex) {
            return showSimpleError(model, "PUT", ex.getMessage(), "500 Internal Server Error");
        }
    }

    @GetMapping("/projects/remove-user")
    public String removeUserFromProjectPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("removeUserRequest", new DeleteProjectRequest());
        return "project-service/remove-user-from-project";
    }

    @PostMapping("/projects/remove-user")
    public String openRemoveUserFromProject(@ModelAttribute("removeUserRequest") DeleteProjectRequest removeUserRequest,
                                            Model model,
                                            HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(removeUserRequest.getProjectId())) {
            return showSimpleError(model, "PATCH", "Project ID must be a positive number.", "400 Bad Request");
        }

        return "redirect:/ui/projects/" + removeUserRequest.getProjectId() + "/user/remove";
    }

    @GetMapping({"/projects/remove-user/{projectId}", "/projects/{projectId}/user/remove"})
    public String removeUserFromProject(@PathVariable String projectId, Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedProjectId = parseId(projectId, "Project ID");
            ProjectResponseDto updatedProject = projectService.removeUserFromProject(parsedProjectId, getToken(session));
            prepareResultPage(model, true, "PATCH", "User removed from project successfully.", null, updatedProject,
                    null, null);
            return "project-service/result";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "PATCH", "Unable to remove the user from the project.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "PATCH", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            return showSimpleError(model, "PATCH", ex.getMessage());
        } catch (Exception ex) {
            return showSimpleError(model, "PATCH", ex.getMessage());
        }
    }

    @GetMapping("/projects/change-user")
    public String changeProjectUserPage(Model model, HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("changeUserRequest", new ChangeUserRequest());
        return "project-service/change-project-user";
    }

    @PostMapping("/projects/change-user")
    public String openChangeProjectUser(@ModelAttribute("changeUserRequest") ChangeUserRequest changeUserRequest,
                                        Model model,
                                        HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        if (isInvalidId(changeUserRequest.getProjectId())) {
            return showSimpleError(model, "PATCH", "Project ID must be a positive number.", "400 Bad Request");
        }

        if (isInvalidId(changeUserRequest.getUserId())) {
            return showSimpleError(model, "PATCH", "User ID must be a positive number.", "400 Bad Request");
        }

        return "redirect:/ui/projects/" + changeUserRequest.getProjectId() + "/user/" + changeUserRequest.getUserId();
    }

    @GetMapping({"/projects/change-user/{projectId}/{userId}", "/projects/{projectId}/user/{userId}"})
    public String changeProjectUser(@PathVariable String projectId,
                                    @PathVariable String userId,
                                    Model model,
                                    HttpSession session) {
        String redirect = redirectIfNotLoggedIn(session);
        if (redirect != null) {
            return redirect;
        }

        try {
            Integer parsedProjectId = parseId(projectId, "Project ID");
            Integer parsedUserId = parseId(userId, "User ID");
            ProjectResponseDto updatedProject = projectService.changeProjectUser(parsedProjectId, parsedUserId,
                    getToken(session));
            prepareResultPage(model, true, "PATCH", "Project user changed successfully.", null, updatedProject, null,
                    null);
            return "project-service/result";
        } catch (RestClientResponseException ex) {
            if (isUnauthorized(ex, session)) {
                return "redirect:/login";
            }
            return showResultError(model, "PATCH", "Unable to change the project user.", ex);
        } catch (RestClientException ex) {
            return showSimpleError(model, "PATCH", "Backend service is not reachable.");
        } catch (IllegalArgumentException ex) {
            return showSimpleError(model, "PATCH", ex.getMessage());
        } catch (Exception ex) {
            return showSimpleError(model, "PATCH", ex.getMessage());
        }
    }

    private String buildProjectListPage(Model model,
                                        List<ProjectResponseDto> projects,
                                        int requestedPage,
                                        String basePath,
                                        String pageTitle) {
        return buildProjectListPage(model, projects, requestedPage, basePath, pageTitle, true);
    }

    private String buildProjectListPage(Model model,
                                        List<ProjectResponseDto> projects,
                                        int requestedPage,
                                        String basePath,
                                        String pageTitle,
                                        boolean hasSearched) {
        List<ProjectResponseDto> safeProjects = projects == null ? List.of() : projects;
        int totalItems = safeProjects.size();
        int totalPages = totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / PAGE_SIZE);
        int currentPage = Math.max(1, Math.min(requestedPage, totalPages));
        int fromIndex = (currentPage - 1) * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, totalItems);

        List<ProjectResponseDto> currentPageItems = totalItems == 0
                ? List.of()
                : safeProjects.subList(fromIndex, toIndex);

        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("projects", currentPageItems);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("hasPrevious", currentPage > 1);
        model.addAttribute("hasNext", currentPage < totalPages);
        model.addAttribute("previousPage", currentPage - 1);
        model.addAttribute("nextPage", currentPage + 1);
        model.addAttribute("basePath", basePath);
        model.addAttribute("previousPageUrl", basePath + "?page=" + (currentPage - 1));
        model.addAttribute("nextPageUrl", basePath + "?page=" + (currentPage + 1));
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("hasSearched", hasSearched);
        model.addAttribute("userProjectsSearchRequest", new UserProjectsSearchRequest());
        model.addAttribute("showUserSearch",
                !"/ui/projects/all".equals(basePath) && !"/ui/projects/unassigned".equals(basePath));
        return "project-service/project-list";
    }

    private Integer parseId(String value, String label) {
        try {
            Integer parsedValue = Integer.parseInt(value);
            if (isInvalidId(parsedValue)) {
                throw new IllegalArgumentException(label + " must be a positive number.");
            }
            return parsedValue;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " is invalid. Please enter a valid number.");
        }
    }

    private boolean isInvalidId(Integer id) {
        return id == null || id <= 0;
    }

    private void validateDateRange(ProjectRequestDto projectRequestDto, BindingResult bindingResult) {
        if (projectRequestDto.getStartDate() != null
                && projectRequestDto.getEndDate() != null
                && projectRequestDto.getEndDate().isBefore(projectRequestDto.getStartDate())) {
            bindingResult.rejectValue("endDate", "endDate.invalid", "End date cannot be before start date.");
        }
    }

    private UserResponseDto getUserById(Integer userId, HttpSession session) {
        try {
            return projectService.getUserById(userId, getToken(session));
        } catch (Exception ex) {
            return null;
        }
    }

    private void ensureUsernameVisible(UserResponseDto user, Integer userId, HttpSession session) {
        if (user == null || (user.getUsername() != null && !user.getUsername().isBlank())) {
            return;
        }

        try {
            List<ProjectResponseDto> projects = projectService.getProjectsByUser(userId, getToken(session));
            for (ProjectResponseDto project : projects) {
                if (project.getUserName() != null && !project.getUserName().isBlank()) {
                    user.setUsername(project.getUserName());
                    return;
                }
            }
        } catch (Exception ex) {
            // Keep the fallback simple. We only need a visible value on the page.
        }

        user.setUsername("Not available");
    }

    private boolean hasVisibleUsername(UserResponseDto user) {
        return user != null
                && user.getUsername() != null
                && !user.getUsername().isBlank()
                && !"Not available".equalsIgnoreCase(user.getUsername());
    }

    private String redirectIfNotLoggedIn(HttpSession session) {
        if (!hasToken(session)) {
            return "redirect:/login";
        }
        return null;
    }

    private boolean hasToken(HttpSession session) {
        Object token = session.getAttribute(SESSION_TOKEN);
        if (!(token instanceof String value) || value.isBlank()) {
            token = session.getAttribute(LEGACY_SESSION_TOKEN);
        }
        return token instanceof String value && !value.isBlank();
    }

    private String getToken(HttpSession session) {
        Object token = session.getAttribute(SESSION_TOKEN);
        if (!(token instanceof String value) || value.isBlank()) {
            token = session.getAttribute(LEGACY_SESSION_TOKEN);
        }
        return token == null ? null : token.toString();
    }

    private String getUsername(HttpSession session) {
        Object username = session.getAttribute(SESSION_USERNAME);
        if (!(username instanceof String value) || value.isBlank()) {
            username = session.getAttribute(LEGACY_SESSION_USERNAME);
        }
        return username == null ? null : username.toString();
    }

    private boolean isUnauthorized(RestClientResponseException ex, HttpSession session) {
        if (ex.getStatusCode().value() == 401) {
            session.invalidate();
            return true;
        }
        return false;
    }

    private boolean hasValidationErrors(RestClientResponseException ex) {
        return ex.getStatusCode().value() == 400 && ex.getResponseBodyAsString() != null
                && ex.getResponseBodyAsString().contains("\"messages\"");
    }

    private String showResultError(Model model, String method, String titleMessage, RestClientResponseException ex) {
        String errorMessage = extractErrorMessage(ex);
        String statusText = buildStatusText(ex.getStatusCode(), ex.getStatusText());
        prepareResultPage(model, false, method, titleMessage, errorMessage, null, null, null, statusText);
        return "project-service/result";
    }

    private String showSimpleError(Model model, String method, String errorMessage) {
        return showSimpleError(model, method, errorMessage, null);
    }

    private String showSimpleError(Model model, String method, String errorMessage, String httpStatus) {
        prepareResultPage(model, false, method, "Request failed.", errorMessage, null, null, null, httpStatus);
        return "project-service/result";
    }

    private String showValidationError(Model model, String method, BindingResult bindingResult) {
        List<String> errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> errors.add(error.getDefaultMessage()));
        return showValidationError(model, method, errors);
    }

    private String showValidationError(Model model, String method, List<String> errors) {
        String message = errors.isEmpty() ? "Validation failed." : String.join(" ", errors);
        prepareResultPage(model, false, method, "Validation failed.", message, null, null, null, "400 Bad Request");
        return "project-service/result";
    }

    private String showBackendValidationError(Model model, String method, RestClientResponseException ex) {
        List<String> errors = extractMessages(ex);
        String statusText = buildStatusText(ex.getStatusCode(), ex.getStatusText());
        prepareResultPage(model, false, method, "Validation failed.", String.join(" ", errors), null, null, null,
                statusText);
        return "project-service/result";
    }

    private void prepareResultPage(Model model,
                                   boolean success,
                                   String method,
                                   String message,
                                   String errorDetails,
                                   ProjectResponseDto project,
                                   UserResponseDto user,
                                   String extraMessage,
                                   String httpStatus) {
        model.addAttribute("hasResult", true);
        model.addAttribute("success", success);
        model.addAttribute("method", method);
        model.addAttribute("message", message);
        model.addAttribute("errorDetails", errorDetails);
        model.addAttribute("project", project);
        model.addAttribute("user", user);
        model.addAttribute("extraMessage", extraMessage);
        model.addAttribute("httpStatus", httpStatus);
    }

    private void prepareResultPage(Model model,
                                   boolean success,
                                   String method,
                                   String message,
                                   String errorDetails,
                                   ProjectResponseDto project,
                                   UserResponseDto user,
                                   String extraMessage) {
        prepareResultPage(model, success, method, message, errorDetails, project, user, extraMessage, null);
    }

    private String extractErrorMessage(RestClientResponseException ex) {
        String responseBody = ex.getResponseBodyAsString();

        String message = extractJsonValue(responseBody, "message");
        if (message != null && !message.isBlank()) {
            return message;
        }

        Map<String, String> validationMessages = extractValidationMessages(responseBody);
        if (!validationMessages.isEmpty()) {
            List<String> items = new ArrayList<>();
            for (Map.Entry<String, String> entry : validationMessages.entrySet()) {
                items.add(entry.getKey() + ": " + entry.getValue());
            }
            return String.join(", ", items);
        }

        return responseBody == null || responseBody.isBlank()
                ? "Unexpected error from backend."
                : responseBody;
    }

    private List<String> extractMessages(RestClientResponseException ex) {
        List<String> messages = new ArrayList<>();
        String responseBody = ex.getResponseBodyAsString();

        String message = extractJsonValue(responseBody, "message");
        if (message != null && !message.isBlank()) {
            messages.add(message);
        }

        Map<String, String> validationMessages = extractValidationMessages(responseBody);
        if (!validationMessages.isEmpty()) {
            for (Map.Entry<String, String> entry : validationMessages.entrySet()) {
                messages.add(entry.getKey() + ": " + entry.getValue());
            }
        }

        if (messages.isEmpty()) {
            messages.add(extractErrorMessage(ex));
        }

        return messages;
    }

    private String buildStatusText(HttpStatusCode statusCode, String statusText) {
        if (statusText != null && !statusText.isBlank()) {
            return statusCode.value() + " " + statusText;
        }
        return String.valueOf(statusCode.value());
    }

    private String extractJsonValue(String responseBody, String key) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        Matcher matcher = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"").matcher(responseBody);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    private Map<String, String> extractValidationMessages(String responseBody) {
        Map<String, String> validationMessages = new LinkedHashMap<>();
        if (responseBody == null || responseBody.isBlank() || !responseBody.contains("\"messages\"")) {
            return validationMessages;
        }

        int startIndex = responseBody.indexOf("\"messages\"");
        int openBraceIndex = responseBody.indexOf("{", startIndex);
        int closeBraceIndex = responseBody.indexOf("}", openBraceIndex);

        if (openBraceIndex == -1 || closeBraceIndex == -1 || closeBraceIndex <= openBraceIndex) {
            return validationMessages;
        }

        String messagesBlock = responseBody.substring(openBraceIndex + 1, closeBraceIndex);
        Matcher matcher = JSON_FIELD_PATTERN.matcher(messagesBlock);
        while (matcher.find()) {
            validationMessages.put(matcher.group(1), matcher.group(2));
        }

        return validationMessages;
    }

    private void prepareCountSearch(Model model) {
        model.addAttribute("countSearchRequest", new UserProjectsSearchRequest());
        model.addAttribute("showCountSearch", true);
    }

    private void prepareProjectExistsSearch(Model model) {
        model.addAttribute("projectExistsSearchRequest", new DeleteProjectRequest());
        model.addAttribute("showProjectExistsSearch", true);
    }

    private void prepareProjectUserSearch(Model model) {
        model.addAttribute("projectUserSearchRequest", new DeleteProjectRequest());
        model.addAttribute("showProjectUserSearch", true);
    }

    private void prepareSearchPageDefaults(Model model) {
        model.addAttribute("hasResult", false);
        model.addAttribute("success", true);
        model.addAttribute("method", "GET");
        model.addAttribute("message", null);
        model.addAttribute("errorDetails", null);
        model.addAttribute("httpStatus", null);
        model.addAttribute("project", null);
        model.addAttribute("user", null);
        model.addAttribute("extraMessage", null);
    }

    private void prepareNotificationsPage(Model model,
                                          List<NotificationResponseDto> notifications,
                                          boolean hasSearched,
                                          String formError) {
        model.addAttribute("notificationSearchRequest", new UserProjectsSearchRequest());
        model.addAttribute("notifications", notifications == null ? List.of() : notifications);
        model.addAttribute("hasSearchedNotifications", hasSearched);
        model.addAttribute("formError", formError);
    }
}
