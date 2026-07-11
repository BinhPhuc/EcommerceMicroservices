package com.binhphuc.product_service.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
  @JsonProperty("status_code") private int statusCode;
  private String message;
}
