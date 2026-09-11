package com.binhphuc.flash_sale_service.client.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlashSaleItem {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("variant_id")
    private String variantId;
}
