package com.binhphuc.inventory_service.kafka.event.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlashSaleItem {
    private String productId;
    private String variantId;
    private Long stock;
}
