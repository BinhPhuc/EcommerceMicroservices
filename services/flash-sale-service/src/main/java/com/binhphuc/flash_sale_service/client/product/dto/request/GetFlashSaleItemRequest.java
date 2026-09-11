package com.binhphuc.flash_sale_service.client.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetFlashSaleItemRequest {
    @JsonProperty("flash_sale_items")
    List<FlashSaleItem> flashSaleItems;

    @JsonProperty("campaign_id")
    private String campaignId;
}
