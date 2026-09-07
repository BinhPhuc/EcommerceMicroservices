package com.binhphuc.product_service.kafka.event;

import com.binhphuc.product_service.kafka.event.dto.flashsale.FlashSaleItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreWarmItemEvent {
    private List<FlashSaleItem> flashSaleItems;
    private String campaignId;
}
