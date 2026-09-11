package com.binhphuc.order_service.service;

import com.binhphuc.order_service.entity.OrderOutboxVariant;

import java.util.List;

public interface OrderOutboxVariantService {
    List<OrderOutboxVariant> findAllByOutboxId(String outboxId);
}
