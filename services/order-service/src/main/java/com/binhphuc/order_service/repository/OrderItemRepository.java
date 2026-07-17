package com.binhphuc.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.binhphuc.order_service.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, String> {}
