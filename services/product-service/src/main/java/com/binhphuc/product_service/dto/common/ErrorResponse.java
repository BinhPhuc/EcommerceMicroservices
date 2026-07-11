package com.binhphuc.product_service.dto.common;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
  int status;
  String error;
  String message;
  String path;
  Instant timestamp;
}
