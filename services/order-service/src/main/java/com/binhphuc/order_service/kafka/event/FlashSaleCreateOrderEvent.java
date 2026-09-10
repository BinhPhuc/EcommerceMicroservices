package com.binhphuc.order_service.kafka.event;

import com.binhphuc.order_service.kafka.event.dto.order.FlashSaleOrderItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlashSaleCreateOrderEvent {
    private String requestId;
    private String userId;
    private String campaignId;
    private List<FlashSaleOrderItem> items;
}
