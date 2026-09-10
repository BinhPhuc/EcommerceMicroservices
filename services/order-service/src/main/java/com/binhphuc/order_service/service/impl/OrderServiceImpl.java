package com.binhphuc.order_service.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.binhphuc.common_core.context.holder.UserContextHolder;
import com.binhphuc.order_service.entity.OrderOutbox;
import com.binhphuc.order_service.enums.PaymentMethod;
import com.binhphuc.order_service.kafka.command.ChangeOrderStatusCommand;
import com.binhphuc.order_service.kafka.command.CreateFlashSaleOrderCommand;
import com.binhphuc.order_service.repository.OrderOutboxRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.order_service.client.product.ProductClient;
import com.binhphuc.order_service.client.product.dto.request.GetProductByIdsRequest;
import com.binhphuc.order_service.client.product.dto.response.GetProductByIdsResponse;
import com.binhphuc.order_service.dto.order.request.CreateOrderItemRequest;
import com.binhphuc.order_service.dto.order.request.CreateOrderRequest;
import com.binhphuc.order_service.dto.order.response.CreateOrderResponse;
import com.binhphuc.order_service.entity.Order;
import com.binhphuc.order_service.enums.OrderStatus;
import com.binhphuc.order_service.kafka.event.OrderCreatedEvent;
import com.binhphuc.order_service.kafka.producer.OrderEventProducer;
import com.binhphuc.order_service.repository.OrderItemRepository;
import com.binhphuc.order_service.repository.OrderRepository;
import com.binhphuc.order_service.service.OrderService;
import com.binhphuc.order_service.entity.OrderItem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient;
    private final OrderEventProducer orderEventProducer;
    private final OrderOutboxRepository orderOutboxRepository;

    @Override
    @Cacheable(value = "orders", key = "#orderId")
    public Order getById(String orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (!orderOptional.isPresent()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Order with id " + orderId + " not" +
                    " found");
        }
        Order order = orderOptional.get();
        if (order.getIsDeleted()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Order with id " + orderId + " not" +
                    " found");
        }
        return order;
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest) {
        // TODO: check logic again
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

        Order newOrder = Order
                .builder()
                .status(OrderStatus.PENDING)
                .userId(UserContextHolder.getUserContext().getUserId())
                .totalAmount(BigDecimal.valueOf(0))
                .build();
        Order savedOrder = orderRepository.save(newOrder);

        List<OrderItem> orderItems = new ArrayList<>();

        int totalAmount = 0;
        for (CreateOrderItemRequest orderItemRequest : createOrderRequest.getOrderItems()) {
            GetProductByIdsResponse product = productIdMap.get(orderItemRequest.getProductId());
            if (product == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Product with id " +
                        orderItemRequest.getProductId() + " not found");
            }
            String productId = product.getId();
            Integer quantity = orderItemRequest.getQuantity();
            Integer stock = product.getStock();
            String variantId = orderItemRequest.getVariantId();
            if (stock < quantity) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "Product " + product.getName() +
                                " is out of stock");
            }
            Integer price = product.getPrice();
            if (price == null) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "Product " + product.getName() +
                                " has no price");
            }
            totalAmount += quantity * price;
            com.binhphuc.order_service.entity.OrderItem newOrderItem =
                    com.binhphuc.order_service.entity.OrderItem
                            .builder()
                            .productId(productId)
                            .quantity(quantity)
                            .variantId(variantId)
                            .quantity(quantity)
                            .build();
            orderItems.add(newOrderItem);
        }
        savedOrder.setTotalAmount(BigDecimal.valueOf(totalAmount));
        orderRepository.save(savedOrder);
        orderItemRepository.saveAll(orderItems);
        orderEventProducer
                .sendOrderCreatedEvent(OrderCreatedEvent
                        .builder()
                        .orderId(savedOrder.getId())
                        .orderItems(orderItems
                                .stream()
                                .map(orderItem -> com.binhphuc.order_service.kafka.event.dto.order.OrderItem
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
    @Transactional
    public void createFlashSaleOrder(CreateFlashSaleOrderCommand createFlashSaleOrderCommand) {
        String idempotencyKey = createFlashSaleOrderCommand.getRequestId();
        Order newOrder = Order
                .builder()
                .userId(createFlashSaleOrderCommand.getUserId())
                .status(OrderStatus.PENDING)
                .idempotencyKey(idempotencyKey)
                .paymentMethod(PaymentMethod.COD)
                .build();
        Order savedOrder = orderRepository.save(newOrder);
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        List<OrderItem> newOrderItems =
                createFlashSaleOrderCommand.getItems().stream().map(item -> {
                    totalAmount.updateAndGet(v -> v.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
                    return OrderItem.builder()
                            .productId(item.getProductId())
                            .variantId(item.getVariantId())
                            .quantity(item.getQuantity())
                            .orderId(savedOrder.getId())
                            .build();
                }).toList();
        savedOrder.setTotalAmount(totalAmount.get());
        orderItemRepository.saveAll(newOrderItems);
        OrderOutbox newOrderOutbox =
                OrderOutbox.builder().orderId(savedOrder.getId()).processed(false).build();
        orderOutboxRepository.save(newOrderOutbox);
    }

    @Override
    @Transactional
    @CacheEvict(value = "orders", key = "#changeOrderStatusCommand.orderId")
    public void changeOrderStatus(ChangeOrderStatusCommand changeOrderStatusCommand) {
        // TODO: getById cannot use by method in same class, need to refactor to use repository
        //  directly
        String orderId = changeOrderStatusCommand.getOrderId();
        OrderStatus orderStatus = changeOrderStatusCommand.getStatus();
        Order order = getById(orderId);
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }
}
