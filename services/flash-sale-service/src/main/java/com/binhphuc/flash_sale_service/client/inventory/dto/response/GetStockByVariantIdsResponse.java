package com.binhphuc.flash_sale_service.client.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetStockByVariantIdsResponse {
    @JsonProperty("variant_id")
    private String variantId;
    private Long stock;
}
