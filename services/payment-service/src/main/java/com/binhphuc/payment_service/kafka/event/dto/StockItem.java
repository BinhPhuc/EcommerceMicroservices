package com.binhphuc.payment_service.kafka.event.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockItem {
    private String variantId;
    private Integer quantity;
}
