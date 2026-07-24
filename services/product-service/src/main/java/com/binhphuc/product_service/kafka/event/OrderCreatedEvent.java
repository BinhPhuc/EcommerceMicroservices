package com.binhphuc.product_service.kafka.event;

import java.util.List;

import com.binhphuc.product_service.kafka.event.dto.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderCreatedEvent {
    private String orderId;
    private List<OrderItem> orderItems;
}
