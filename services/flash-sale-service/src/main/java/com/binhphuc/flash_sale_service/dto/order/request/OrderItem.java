package com.binhphuc.flash_sale_service.dto.order.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @JsonProperty("product_id")
    private String productId;

    @JsonProperty("variant_id")
    private String variantId;

    @JsonProperty("quantity")
    private Integer quantity;
}
