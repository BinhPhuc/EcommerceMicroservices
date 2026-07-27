package com.binhphuc.product_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binhphuc.common_web_starter.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        log.info("Health check");
        return ResponseEntity.ok(ApiResponse.success("Product service is healthy", "Health check successful"));
    }
}
