package com.binhphuc.order_service.kafka.event.dto.order;

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
