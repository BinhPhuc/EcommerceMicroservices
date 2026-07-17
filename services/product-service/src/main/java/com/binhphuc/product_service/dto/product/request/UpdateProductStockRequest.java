package com.binhphuc.product_service.dto.product.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductStockRequest {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
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
