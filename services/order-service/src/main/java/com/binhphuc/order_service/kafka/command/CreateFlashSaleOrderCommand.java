package com.binhphuc.order_service.kafka.command;

import com.binhphuc.order_service.kafka.event.dto.order.FlashSaleOrderItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateFlashSaleOrderCommand {
    private String requestId;
    private String userId;
    private String campaignId;
    private List<FlashSaleOrderItem> items;
}
