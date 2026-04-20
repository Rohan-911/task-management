package com.frontend.commentservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.frontend.commentservice.dto.CommentDTO;
import com.frontend.config.JwtInterceptor;

@Service
public class CommentService {

    private final RestClient restClient;
    private final JwtInterceptor jwtInterceptor;

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    public CommentService(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.restClient = RestClient.create();
    }

    // GET ALL
    public List<CommentDTO> getAll() {
        String url = backendUrl + "/api/comments";
        return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentDTO>>() {});
    }

    // GET BY ID
    public CommentDTO getById(Integer id) {
        String url = backendUrl + "/api/comments/" + id;
    /*    return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                .retrieve()
                .body(CommentDTO.class);
    }*/
    
    return restClient.get()
            .uri(url)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(CommentDTO.class);
    }
    // CREATE
    public CommentDTO create(CommentDTO c) {
        String url = backendUrl + "/api/comments";
        return jwtInterceptor.addAuthHeader(restClient.post().uri(url).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(c))
                .retrieve()
                .body(CommentDTO.class);
    }

    // DELETE
    public void delete(Integer id) {
        String url = backendUrl + "/api/comments/" + id;
        jwtInterceptor.addAuthHeader(restClient.delete().uri(url))
                .retrieve()
                .toBodilessEntity();
    }

    // GET BY TASK
    public List<CommentDTO> getByTask(Integer taskId) {
        String url = backendUrl + "/api/comments/task/" + taskId;
        return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentDTO>>() {});
    }

    // GET BY USER
    public List<CommentDTO> getByUser(Integer userId) {
        String url = backendUrl + "/api/comments/user/" + userId;
        return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentDTO>>() {});
    }
}