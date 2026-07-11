package com.binhphuc.product_service.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonPropertyOrder({"status_code", "error", "message", "path", "timestamp"})
public class ErrorResponse {
  @JsonProperty("status_code") private int statusCode;
  private String error;
  private String message;
  private String path;
  private Instant timestamp;
}
