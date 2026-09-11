package com.binhphuc.inventory_service.kafka.command;

import com.binhphuc.inventory_service.kafka.event.dto.FlashSaleItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CacheStockCommand {
    private List<FlashSaleItem> flashSaleItems;
    private String campaignId;
}
