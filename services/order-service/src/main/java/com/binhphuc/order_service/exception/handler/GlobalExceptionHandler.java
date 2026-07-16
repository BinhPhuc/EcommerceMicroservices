package com.binhphuc.product_service.exception.handler;

import com.binhphuc.product_service.dto.common.ErrorResponse;
import com.binhphuc.product_service.exception.ResourceExistException;
import com.binhphuc.product_service.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException exception,
      HttpServletRequest request) {
    ErrorResponse response = ErrorResponse.builder()
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
    ErrorResponse response = ErrorResponse.builder()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
        .message(exception.getMessage())
        .path(request.getRequestURI())
        .timestamp(java.time.Instant.now())
        .build();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }
}
