package com.binhphuc.flash_sale_service.kafka.event;

import com.binhphuc.flash_sale_service.kafka.event.dto.StockItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent {
    private String idempotencyKey;
    private String campaignId;
    private List<StockItem> items;
}
