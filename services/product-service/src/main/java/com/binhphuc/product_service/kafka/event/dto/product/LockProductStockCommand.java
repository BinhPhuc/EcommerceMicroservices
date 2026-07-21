package com.binhphuc.product_service.kafka.event.dto.product;

import java.util.List;

import com.binhphuc.product_service.kafka.event.dto.order.OrderItem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LockProductStockCommand {
    private String orderId;
    private List<OrderItem> orderItems;
}
