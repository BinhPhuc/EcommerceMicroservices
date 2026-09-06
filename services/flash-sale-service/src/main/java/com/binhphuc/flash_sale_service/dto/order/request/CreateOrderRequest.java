package com.binhphuc.flash_sale_service.dto.order.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    @JsonProperty("campaign_id")
    private String campaignId;

    private String customerId;

    @JsonProperty("items")
    private List<OrderItem> items;
}
