package com.binhphuc.order_service.kafka.event;

import com.binhphuc.order_service.kafka.event.dto.order.StockItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChargePaymentEvent {
    private String orderId;
    private String idempotencyKey;
    private String campaignId;
    private List<StockItem> items;
}
