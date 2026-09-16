package com.binhphuc.flash_sale_service.client.product.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProductResponse {
    private String name;

    private String description;

    @JsonProperty("units_sold")
    private Long unitsSold;

    private String sku;

    // TODO: 1 product should have multiple variants, so we should change this to a list of
    //  variants
    private Map<String, String> variants;

    private BigDecimal price;

    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("variant_id")
    private String variantId;
}
