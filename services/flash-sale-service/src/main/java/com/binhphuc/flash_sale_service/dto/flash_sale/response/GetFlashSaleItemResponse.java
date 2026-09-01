package com.binhphuc.flash_sale_service.dto.flash_sale.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetFlashSaleItemResponse {
    private String id;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("variant_id")
    private String variantId;

    @JsonProperty("flash_price")
    private BigDecimal flashPrice;

    private Long stock;

    @JsonProperty("sold_quantity")
    private Long soldQuantity;

    @JsonProperty("purchase_limit")
    private Integer purchaseLimit;
}
