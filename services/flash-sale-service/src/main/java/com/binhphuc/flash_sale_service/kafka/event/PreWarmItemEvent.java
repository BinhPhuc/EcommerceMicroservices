package com.binhphuc.flash_sale_service.kafka.event;

import com.binhphuc.flash_sale_service.kafka.event.dto.FlashSaleItem;
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
