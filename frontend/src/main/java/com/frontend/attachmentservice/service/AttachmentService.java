package com.frontend.attachmentservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.frontend.attachmentservice.dto.AttachmentDTO;
import com.frontend.config.JwtInterceptor;

@Service
public class AttachmentService {

    private final RestClient restClient;
    private final JwtInterceptor jwtInterceptor;

    @Value("${backend.url:http://localhost:8080}")
    private String backendUrl;

    public AttachmentService(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        this.restClient = RestClient.create();
    }

    // GET ALL
    public List<AttachmentDTO> getAll() {
        try {
            String url = backendUrl + "/api/attachments";
            return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AttachmentDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch attachments: " + e.getMessage());
        }
    }

    // GET PAGINATED
    public com.frontend.commentservice.dto.PagedResponse<AttachmentDTO> getAllPaginated(int page, int size) {
        try {
            String url = backendUrl + "/api/attachments/list/all?page=" + page + "&size=" + size;
            return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .body(new ParameterizedTypeReference<com.frontend.commentservice.dto.PagedResponse<AttachmentDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch paginated attachments: " + e.getMessage());
        }
    }

    // GET BY ID
    public AttachmentDTO getById(Integer id) {
        try {
            String url = backendUrl + "/api/attachments/item/" + id;
            return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, res) -> {
                        throw new RuntimeException("Attachment not found with ID: " + id);
                    })
                    .body(AttachmentDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // CREATE
    public AttachmentDTO create(AttachmentDTO a) {
        String url = backendUrl + "/api/attachments";
        return jwtInterceptor.addAuthHeader(restClient.post().uri(url).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).body(a))
                .retrieve()
                .body(AttachmentDTO.class);
    }

    // DELETE
    public void delete(Integer id) {
        try {
            String url = backendUrl + "/api/attachments/item/" + id;
            jwtInterceptor.addAuthHeader(restClient.delete().uri(url))
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, res) -> {
                        throw new RuntimeException("Cannot delete. Attachment not found with ID: " + id);
                    })
                    .toBodilessEntity();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // GET BY TASK
    public List<AttachmentDTO> getByTask(Integer taskId) {
        String url = backendUrl + "/api/attachments/task/" + taskId;
        return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                .retrieve()
                .body(new ParameterizedTypeReference<List<AttachmentDTO>>() {});
    }

    // COUNT BY TASK
    public Integer countByTask(Integer taskId) {
        String url = backendUrl + "/api/attachments/count/" + taskId;
        return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                .retrieve()
                .body(Integer.class);
    }

    // DELETE BY TASK
    public void deleteByTask(Integer taskId) {
        String url = backendUrl + "/api/attachments/task/" + taskId;
        jwtInterceptor.addAuthHeader(restClient.delete().uri(url))
                .retrieve()
                .toBodilessEntity();
    }
}