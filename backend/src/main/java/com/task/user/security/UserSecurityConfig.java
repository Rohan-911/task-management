package com.task.user.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class UserSecurityConfig {

	private final JwtFilter jwtFilter;

	public UserSecurityConfig(JwtFilter jwtFilter) {
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				// ⭐ Stateless (JWT)
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authorizeHttpRequests(auth -> auth

					    // ✅ PUBLIC UI PAGES
					    .requestMatchers(
					            "/",                  // login page
					            "/dashboard",
					            "/admin-dashboard",
					            "/user-dashboard",
					            "/users-view",
					            "/create-user",
					            "/css/**",
					            "/js/**"
					    ).permitAll()

					    // ✅ LOGIN API
					    .requestMatchers("/api/auth/**").permitAll()

					    // 🔐 PROTECTED APIs
					    .requestMatchers("/api/**").authenticated()

					    // fallback
					    .anyRequest().permitAll()
					)

				.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.getWriter().write("Unauthorized: Invalid or missing token");
				}))

				// ⭐ JWT FILTER
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}