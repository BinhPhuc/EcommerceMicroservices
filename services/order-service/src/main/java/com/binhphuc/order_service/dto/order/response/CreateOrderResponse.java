package com.binhphuc.order_service.dto.order.response;

import com.binhphuc.order_service.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponse {
  private OrderStatus status;

  @JsonProperty("total_amount")
  private Integer totalAmount;
}
