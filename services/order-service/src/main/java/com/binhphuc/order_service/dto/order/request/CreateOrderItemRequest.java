package com.binhphuc.order_service.dto.order.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderItemRequest {
  @JsonProperty("product_id")
  private String productID;

  private Integer quantity;
}
