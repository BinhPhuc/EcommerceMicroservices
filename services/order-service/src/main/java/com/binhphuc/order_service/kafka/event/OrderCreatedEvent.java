package com.binhphuc.order_service.kafka.event;

import java.util.List;

import com.binhphuc.order_service.kafka.event.dto.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEvent {
    private String orderId;
    private List<OrderItem> orderItems;
}
