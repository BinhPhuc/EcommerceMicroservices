package com.binhphuc.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binhphuc.auth_service.dto.auth.UserRegistrationDTO;
import com.binhphuc.auth_service.service.UserService;
import com.binhphuc.common_web_starter.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        log.info("Registering user: {}", userRegistrationDTO.getUsername());
        userService.createUser(userRegistrationDTO);
        return ResponseEntity.ok(ApiResponse.created("User registered successfully"));
    }
}
