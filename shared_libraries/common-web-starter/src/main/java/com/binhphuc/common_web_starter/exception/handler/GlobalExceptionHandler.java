package com.binhphuc.common_web_starter.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.binhphuc.common_web_starter.dto.ErrorResponse;
import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.common_web_starter.exception.ResourceExistException;
import com.binhphuc.common_web_starter.exception.ResourceNotFoundException;


import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception,
                                                                HttpServletRequest request) {
        ErrorResponse response = ErrorResponse
                .builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(java.time.Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ResourceExistException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceExistException exception,
                                                                HttpServletRequest request) {
        ErrorResponse response = ErrorResponse
                .builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(java.time.Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBussinessException(BusinessException exception,
                                                                  HttpServletRequest request) {
        ErrorResponse response = ErrorResponse
                .builder()
                .statusCode(exception.getHttpStatus().value())
                .error(exception.getHttpStatus().getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(java.time.Instant.now())
                .build();
        return ResponseEntity.status(exception.getHttpStatus()).body(response);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponseException(WebClientResponseException exception,
                                                                          HttpServletRequest request) {
        ErrorResponse response = ErrorResponse
                .builder()
                .statusCode(exception.getStatusCode().value())
                .error(exception.getStatusText())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .timestamp(java.time.Instant.now())
                .build();
        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }
}
