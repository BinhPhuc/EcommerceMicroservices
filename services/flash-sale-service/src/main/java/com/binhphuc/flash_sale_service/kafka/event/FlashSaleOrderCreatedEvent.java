package com.binhphuc.flash_sale_service.kafka.event;

import com.binhphuc.flash_sale_service.kafka.event.dto.FlashSaleOrderItem;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlashSaleOrderCreatedEvent {
    private String requestId;
    private String userId;
    private String campaignId;
    private Instant createdAt;
    private List<FlashSaleOrderItem> items;
}
