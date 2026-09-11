package com.binhphuc.order_service.kafka.event.dto.order;

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
