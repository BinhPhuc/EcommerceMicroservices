package com.binhphuc.inventory_service.kafka.event.dto;

import lombok.*;

import java.math.BigDecimal;

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
