package com.frontend.userservice.controller;

import com.frontend.userservice.dto.AuthResponseDTO;
import com.frontend.userservice.dto.LoginRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;

@Controller
public class AuthController {

    private final RestClient restClient;

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    public AuthController() {
        this.restClient = RestClient.create();
    }

    @PostMapping("/login-action")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam(required = false) String redirect,
                        @RequestParam(required = false) String loginPage,
                        HttpServletRequest request,
                        Model model) {

        try {
            LoginRequestDTO loginReq = new LoginRequestDTO();
            loginReq.setUsername(username);
            loginReq.setPassword(password);

            AuthResponseDTO response = restClient.post()
                    .uri(backendUrl + "/api/auth/login")
                    .body(loginReq)
                    .retrieve()
                    .body(AuthResponseDTO.class);

            if (response != null && response.getToken() != null) {

                HttpSession session = request.getSession();
                session.setAttribute("JWT_TOKEN", response.getToken());
                session.setAttribute("USERNAME", username);
                session.setAttribute("ROLES", response.getRoles());
                
                boolean isAdmin = response.getRoles() != null &&
                        response.getRoles().stream()
                                .anyMatch(r -> r.equalsIgnoreCase("admin") || r.equalsIgnoreCase("ROLE_ADMIN"));

                session.setAttribute("isAdmin", isAdmin);

                if (redirect != null && !redirect.isBlank()) {
                    return "redirect:" + redirect;
                }

                return isAdmin ? "redirect:/admin-dashboard" : "redirect:/user-dashboard";

            } else {
                model.addAttribute("error", "Invalid credentials");
                return (loginPage != null) ? loginPage : "userservice/login";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Login failed: " + e.getMessage());
            return (loginPage != null) ? loginPage : "userservice/login";
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/comment/login";
    }
}
