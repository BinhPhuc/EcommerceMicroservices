package com.binhphuc.order_service.service;

import com.binhphuc.order_service.entity.OrderOutbox;

import java.util.List;

public interface OrderOutboxService {
    void setOutboxProcessed(OrderOutbox orderOutbox);

    List<OrderOutbox> getListUnprocessedOrderOutbox();
}
