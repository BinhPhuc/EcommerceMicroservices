package com.binhphuc.flash_sale_service.dto.flash_sale.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCampaignItemResponse {
    private String id;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("variant_id")
    private String variantId;

    private BigDecimal price;

    private Long stock;

    @JsonProperty("sold_quantity")
    private Long soldQuantity;

    private String productName;

    private String description;

    private Map<String, String> variants;
}
