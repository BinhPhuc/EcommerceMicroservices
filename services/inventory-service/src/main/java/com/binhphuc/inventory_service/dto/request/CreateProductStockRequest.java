package com.binhphuc.inventory_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductStockRequest {
    private Long stock;

    @JsonProperty("variant_id")
    private String variantId;
}
