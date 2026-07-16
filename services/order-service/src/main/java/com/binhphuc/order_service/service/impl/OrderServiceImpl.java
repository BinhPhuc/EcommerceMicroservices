package com.binhphuc.order_service.service.impl;

import org.springframework.stereotype.Service;

import com.binhphuc.order_service.dto.order.request.CreateOrderItemRequest;
import com.binhphuc.order_service.dto.order.request.CreateOrderRequest;
import com.binhphuc.order_service.dto.order.response.CreateOrderResponse;
import com.binhphuc.order_service.entity.Order;
import com.binhphuc.order_service.entity.OrderItem;
import com.binhphuc.order_service.enums.OrderStatus;
import com.binhphuc.order_service.repository.OrderItemRepository;
import com.binhphuc.order_service.repository.OrderRepository;
import com.binhphuc.order_service.service.OrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;

  @Override
  @Transactional
  public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
    Order newOrder = new Order();
    // TODO: validate order request later
    int totalAmount = 0;
    newOrder.setStatus(OrderStatus.PENDING);
    newOrder.setCustomerID(createOrderRequest.getCustomerID());
    for (CreateOrderItemRequest orderItemRequest : createOrderRequest.getOrderItems()) {
      // TODO: get order item price from product service later
      // totalAmount += orderItemRequest.getPrice() * orderItemRequest.getQuantity();
      totalAmount += 100 * orderItemRequest.getQuantity(); // hardcode for now
    }
    newOrder.setTotalAmount(totalAmount);
    Order savedOrder = orderRepository.save(newOrder);
    String newOrderID = savedOrder.getId();
    for (CreateOrderItemRequest orderItemRequest : createOrderRequest.getOrderItems()) {
      OrderItem newOrderItem = OrderItem.builder().orderID(newOrderID).productID(orderItemRequest.getProductID()).quantity(orderItemRequest.getQuantity()).build();
      orderItemRepository.save(newOrderItem);
    }
    return CreateOrderResponse.builder().status(savedOrder.getStatus()).totalAmount(savedOrder.getTotalAmount()).build();
  }
}
