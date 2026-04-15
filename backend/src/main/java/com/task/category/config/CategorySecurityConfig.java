package com.task.category.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class CategorySecurityConfig {

    @Bean
    public SecurityFilterChain categorysecurityFilterChain(HttpSecurity http) throws Exception {
        http
        .securityMatcher("/api/category/**") 
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}