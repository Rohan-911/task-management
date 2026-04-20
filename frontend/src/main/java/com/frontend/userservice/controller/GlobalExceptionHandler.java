package com.frontend.userservice.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice(basePackages = "com.frontend.userservice.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientException(HttpClientErrorException ex, HttpServletRequest request) {
        
        if (ex.getStatusCode().value() == 401 || ex.getStatusCode().value() == 403) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return "redirect:/login";
        }
        
        // Fallback for other client errors (e.g. 400, 404)
        // Rethrow so the controller's try-catch can handle it and display a specific error
        throw ex;
    }

    @ExceptionHandler({
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
        org.springframework.web.bind.MissingServletRequestParameterException.class
    })
    public String handleBadRequests(Exception ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String safeRedirect = "/dashboard"; // default fallback

        if (uri.contains("/api/users")) {
            if (uri.contains("/edit")) safeRedirect = "/api/users/edit";
            else if (uri.contains("/delete")) safeRedirect = "/api/users/delete";
            else if (uri.contains("/roles")) safeRedirect = "/api/users/roles";
            else if (uri.contains("/assign-roles")) safeRedirect = "/api/users/assign-roles";
            else safeRedirect = "/api/users/find";
        }

        return "redirect:" + safeRedirect + "?error=Invalid input. Please provide a valid number.";
    }
}
