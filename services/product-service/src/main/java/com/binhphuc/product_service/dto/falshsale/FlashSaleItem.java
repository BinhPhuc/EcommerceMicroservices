package com.binhphuc.product_service.dto.falshsale;

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
