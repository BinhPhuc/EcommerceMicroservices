package com.binhphuc.order_service.service;

import com.binhphuc.order_service.dto.order.request.CreateOrderRequest;
import com.binhphuc.order_service.dto.order.response.CreateOrderResponse;
import com.binhphuc.order_service.enums.OrderStatus;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest);

    void changeOrderStatus(String orderId, OrderStatus orderStatus);
}
