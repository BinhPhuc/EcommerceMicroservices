package com.binhphuc.product_service.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseResponse {
  @JsonProperty("status_code") private int statusCode;
  private String message;
}
