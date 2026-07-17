package com.binhphuc.order_service.dto.order.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderItemRequest {
    @NotEmpty
    @JsonProperty("product_id")
    private String productId;

    @NotNull
    @Positive
    private Integer quantity;
}
