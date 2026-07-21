package com.binhphuc.order_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.order_service.client.product.ProductClient;
import com.binhphuc.order_service.client.product.dto.request.GetProductByIdsRequest;
import com.binhphuc.order_service.client.product.dto.response.GetProductByIdsResponse;
import com.binhphuc.order_service.dto.order.request.CreateOrderItemRequest;
import com.binhphuc.order_service.dto.order.request.CreateOrderRequest;
import com.binhphuc.order_service.dto.order.response.CreateOrderResponse;
import com.binhphuc.order_service.entity.Order;
import com.binhphuc.order_service.entity.OrderItem;
import com.binhphuc.order_service.enums.OrderStatus;
import com.binhphuc.order_service.kafka.event.OrderCreatedEvent;
import com.binhphuc.order_service.kafka.event.OrderCreatedEvent.OrderItemEvent;
import com.binhphuc.order_service.kafka.producer.OrderEventProducer;
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
    private final ProductClient productClient;
    private final OrderEventProducer orderEventProducer;

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        List<String> productIds = createOrderRequest
                .getOrderItems()
                .stream()
                .map(CreateOrderItemRequest::getProductId)
                .toList();
        List<GetProductByIdsResponse> getProductByIdsResponses = productClient
                .getProductsByIds(GetProductByIdsRequest.builder().productIds(productIds).build());

        Map<String, GetProductByIdsResponse> productIdMap = getProductByIdsResponses
                .stream()
                .collect(Collectors.toMap(GetProductByIdsResponse::getId, product -> product));

        Order newOrder = new Order();
        newOrder.setStatus(OrderStatus.PENDING);
        newOrder.setCustomerId(createOrderRequest.getCustomerId());
        newOrder.setTotalAmount(0);
        Order savedOrder = orderRepository.save(newOrder);

        List<OrderItem> orderItems = new ArrayList<>();

        int totalAmount = 0;
        for (CreateOrderItemRequest orderItemRequest : createOrderRequest.getOrderItems()) {
            GetProductByIdsResponse product = productIdMap.get(orderItemRequest.getProductId());
            if (product == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Product with id " +
                        orderItemRequest.getProductId() + " not found");
            }

            Integer quantity = orderItemRequest.getQuantity();
            Integer stock = product.getStock();

            if (stock < quantity) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Product " + product.getName() +
                        " is out of stock");
            }

            Integer price = product.getPrice();
            if (price == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Product " + product.getName() +
                        " has no price");
            }

            totalAmount += quantity * price;

            OrderItem newOrderItem = OrderItem
                    .builder()
                    .orderId(savedOrder.getId())
                    .productId(product.getId())
                    .quantity(quantity)
                    .price(price)
                    .build();
            orderItems.add(newOrderItem);
        }

        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);
        orderItemRepository.saveAll(orderItems);

        orderEventProducer
                .sendOrderCreatedEvent(OrderCreatedEvent
                        .builder()
                        .orderId(savedOrder.getId())
                        .orderItems(orderItems
                                .stream()
                                .map(orderItem -> OrderItemEvent
                                        .builder()
                                        .productId(orderItem.getProductId())
                                        .quantity(orderItem.getQuantity())
                                        .build()
                                )
                                .toList())
                        .build());

        return CreateOrderResponse
                .builder()
                .status(savedOrder.getStatus())
                .totalAmount(savedOrder.getTotalAmount())
                .build();
    }

    @Override
    public void changeOrderStatus(String orderId, OrderStatus orderStatus) {
        Order order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Order with id " + orderId +
                        " not found"));
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }
}
