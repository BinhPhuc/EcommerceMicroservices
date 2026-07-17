package com.binhphuc.order_service.client.product.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductStockRequest {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateFilter {
        @JsonProperty("product_id")
        @NotEmpty
        private String productId;

        @NotNull
        private Integer quantity;
    }

    @JsonProperty("products")
    List<UpdateFilter> products;
}
