package com.binhphuc.product_service.client.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProductStockRequest {
    private Integer stock;

    @JsonProperty("variant_id")
    private String variantId;
}
