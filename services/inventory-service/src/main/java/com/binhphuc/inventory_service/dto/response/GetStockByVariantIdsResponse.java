package com.binhphuc.inventory_service.dto.response;

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
