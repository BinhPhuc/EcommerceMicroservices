package com.binhphuc.product_service.kafka.event.dto.product;

import java.util.List;

import com.binhphuc.product_service.kafka.event.dto.order.OrderItem;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LockProductStockCommand {
    private String orderId;
    private List<OrderItem> orderItems;
}
