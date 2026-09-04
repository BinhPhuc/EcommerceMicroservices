package com.binhphuc.product_service.kafka.command;

import com.binhphuc.product_service.kafka.event.dto.flashsale.FlashSaleItem;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreWarmItemCommand {
    private List<FlashSaleItem> flashSaleItems;
}
