package com.binhphuc.common_web_starter.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
  @JsonProperty("status_code")
  private int statusCode;

  private String error;

  private String message;

  private String path;

  private Instant timestamp;
}
