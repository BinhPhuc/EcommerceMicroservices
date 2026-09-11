package com.binhphuc.flash_sale_service.kafka.event;

import com.binhphuc.flash_sale_service.kafka.event.dto.FlashSaleOrderItem;
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
    private String username;
    private String campaignId;
    private List<FlashSaleOrderItem> items;
}