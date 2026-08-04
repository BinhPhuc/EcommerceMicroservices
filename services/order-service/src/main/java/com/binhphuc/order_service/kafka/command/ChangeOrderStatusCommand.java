package com.binhphuc.order_service.kafka.command;

import com.binhphuc.order_service.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeOrderStatusCommand {
    private String orderId;
    private OrderStatus orderStatus;
}
