package com.binhphuc.product_service.dto.product.response;

import com.binhphuc.product_service.dto.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateProductResponse extends BaseResponse {
  @JsonProperty("product_id") private String productId;
}
