package com.frontend.category.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.frontend.category.dto.AuthResponseDTO;
import com.frontend.category.dto.LoginRequestDTO;

@HttpExchange("/api/auth")
public interface AuthClient {

    @PostExchange("/login")
    AuthResponseDTO login(@RequestBody LoginRequestDTO requestDto);

}
