package com.binhphuc.order_service.service;

import com.binhphuc.order_service.dto.order.request.CreateOrderRequest;
import com.binhphuc.order_service.dto.order.response.CreateOrderResponse;
import com.binhphuc.order_service.enums.OrderStatus;
import com.binhphuc.order_service.kafka.event.dto.order.ChangeOrderStatusCommand;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest);

    void changeOrderStatus(ChangeOrderStatusCommand changeOrderStatusCommand);
}
