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
        String url = backendUrl + "/api/attachments";
        return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                .retrieve()
                .body(new ParameterizedTypeReference<List<AttachmentDTO>>() {});
    }

    // GET BY ID
    public AttachmentDTO getById(Integer id) {
        String url = backendUrl + "/api/attachments/" + id;
        return jwtInterceptor.addAuthHeader(restClient.get().uri(url).accept(MediaType.APPLICATION_JSON))
                .retrieve()
                .body(AttachmentDTO.class);
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
        String url = backendUrl + "/api/attachments/" + id;
        jwtInterceptor.addAuthHeader(restClient.delete().uri(url))
                .retrieve()
                .toBodilessEntity();
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