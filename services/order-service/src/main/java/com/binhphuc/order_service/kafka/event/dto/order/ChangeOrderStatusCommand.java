package com.binhphuc.order_service.kafka.event.dto.order;

import com.binhphuc.order_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeOrderStatusCommand {
    private String orderId;
    private OrderStatus orderStatus;
}
