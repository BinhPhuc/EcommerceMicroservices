package com.binhphuc.flash_sale_service.dto.flash_sale.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateFlashSaleItemRequest {
    @JsonProperty("product_id")
    @NotEmpty
    private String productId;

    @JsonProperty("variant_id")
    @NotEmpty
    private String variantId;

    @JsonProperty("flash_price")
    @NotNull
    @Positive
    private BigDecimal flashPrice;

    @NotNull
    @Positive
    private Long stock;

    @JsonProperty("purchase_limit")
    @NotNull
    @Positive
    private Integer purchaseLimit;
}
