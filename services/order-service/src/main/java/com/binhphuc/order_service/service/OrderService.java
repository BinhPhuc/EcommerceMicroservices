package com.binhphuc.order_service.service;

import com.binhphuc.order_service.dto.order.request.CreateOrderRequest;
import com.binhphuc.order_service.dto.order.response.CreateOrderResponse;
import com.binhphuc.order_service.entity.Order;
import com.binhphuc.order_service.kafka.command.ChangeOrderStatusCommand;
import com.binhphuc.order_service.kafka.command.CreateFlashSaleOrderCommand;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest);

    void changeOrderStatus(ChangeOrderStatusCommand changeOrderStatusCommand);

    Order getById(String orderId);

    void createFlashSaleOrder(CreateFlashSaleOrderCommand createFlashSaleOrderCommand);
}
