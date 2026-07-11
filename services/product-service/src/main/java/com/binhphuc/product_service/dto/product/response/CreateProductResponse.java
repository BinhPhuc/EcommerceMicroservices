package com.binhphuc.product_service.dto.product.response;

import com.binhphuc.product_service.dto.common.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductResponse extends BaseResponse {
  private String name;
}
