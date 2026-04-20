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
        try {
            String url = backendUrl + "/api/comments";
            return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CommentDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch comments: " + e.getMessage());
        }
    }

    // GET PAGINATED
    public com.frontend.commentservice.dto.PagedResponse<CommentDTO> getAllPaginated(int page, int size) {
        try {
            String url = backendUrl + "/api/comments/list/all?page=" + page + "&size=" + size;
            return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .body(new ParameterizedTypeReference<com.frontend.commentservice.dto.PagedResponse<CommentDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch paginated comments: " + e.getMessage());
        }
    }

    // GET BY ID
    public CommentDTO getById(Integer id) {
        try {
            String url = backendUrl + "/api/comments/item/" + id;
            return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, res) -> {
                        throw new RuntimeException("Comment not found with ID: " + id);
                    })
                    .body(CommentDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    // CREATE
    public CommentDTO create(CommentDTO c) {
        try {
            String url = backendUrl + "/api/comments";
            return jwtInterceptor.addAuthHeader(restClient.post().uri(url).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(c))
                    .retrieve()
                    .body(CommentDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create comment: " + e.getMessage());
        }
    }

    // DELETE
    public void delete(Integer id) {
        try {
            String url = backendUrl + "/api/comments/item/" + id;
            jwtInterceptor.addAuthHeader(restClient.delete().uri(url))
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, res) -> {
                        throw new RuntimeException("Cannot delete. Comment not found with ID: " + id);
                    })
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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