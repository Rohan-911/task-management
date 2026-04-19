package com.frontend.config;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtInterceptor {

    public RestClient.RequestHeadersSpec<?> addAuthHeader(RestClient.RequestHeadersSpec<?> spec) {

        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attr != null) {
            HttpServletRequest request = attr.getRequest();
            String token = (String) request.getSession().getAttribute("JWT_TOKEN");

            System.out.println("INTERCEPTOR TOKEN = " + token); // 🔥 DEBUG

            if (token != null) {
                return spec.header("Authorization", "Bearer " + token);
            }
        }

        return spec;
    }
}