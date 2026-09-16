package com.binhphuc.flash_sale_service.client.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetStockByVariantIdsRequest {
    @JsonProperty("variant_ids")
    private List<String> variantIds;
}
