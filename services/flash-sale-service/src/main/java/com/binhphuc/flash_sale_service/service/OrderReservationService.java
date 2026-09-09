package com.binhphuc.flash_sale_service.service;

import com.binhphuc.flash_sale_service.kafka.event.FlashSaleOrderCreatedEvent;

public interface OrderReservationService {
    void reserve(FlashSaleOrderCreatedEvent event);

    void release(FlashSaleOrderCreatedEvent event);
}
