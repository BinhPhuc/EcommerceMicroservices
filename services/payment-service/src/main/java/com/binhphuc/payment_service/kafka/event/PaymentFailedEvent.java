package com.binhphuc.payment_service.kafka.event;

import com.binhphuc.payment_service.kafka.event.dto.StockItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentFailedEvent {
    private String orderId;
    private String idempotencyKey;
    private String campaignId;
    private List<StockItem> items;
}
