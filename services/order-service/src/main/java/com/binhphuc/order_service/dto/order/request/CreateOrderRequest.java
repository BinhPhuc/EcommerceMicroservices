package com.binhphuc.order_service.dto.order.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
  @JsonProperty("customer_id")
  // TODO: review: user upload via body or get it from SecurityContextHolder
  private String customerID;

  @JsonProperty("order_items")
  private List<CreateOrderItemRequest> orderItems;
}
