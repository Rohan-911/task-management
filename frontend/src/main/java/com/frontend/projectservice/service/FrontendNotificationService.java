package com.frontend.projectservice.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.frontend.projectservice.dto.NotificationResponseDto;

@Service
public class FrontendNotificationService {

    public List<NotificationResponseDto> getNotificationsByUser(Integer userId, String token) {
        return notificationClient(token).get()
                .uri("/user/{userId}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<NotificationResponseDto>>() {
                });
    }

    private RestClient notificationClient(String token) {
        return RestClient.builder()
                .baseUrl("http://localhost:8080/api/notifications")
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }
}
