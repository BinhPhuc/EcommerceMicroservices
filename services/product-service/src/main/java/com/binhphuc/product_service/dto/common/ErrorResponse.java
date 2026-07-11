package com.binhphuc.product_service.dto.common;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponse {
  int status;
  String error;
  String message;
  String path;
  Instant timestamp;
}
