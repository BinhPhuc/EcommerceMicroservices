package com.binhphuc.product_service.kafka.event.dto.order;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    private String productId;
    private int quantity;
}
