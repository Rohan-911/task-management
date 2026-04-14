package com.task.taskservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TaskSecurityConfig {

    @Bean
    public SecurityFilterChain taskfilterChain(HttpSecurity http) throws Exception {

        http
        	.securityMatcher("/api/tasks/**") 
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}