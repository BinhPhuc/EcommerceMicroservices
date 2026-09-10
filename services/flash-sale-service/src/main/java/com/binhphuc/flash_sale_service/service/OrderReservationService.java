package com.binhphuc.flash_sale_service.service;

import com.binhphuc.flash_sale_service.kafka.event.FlashSaleCreateOrderEvent;

public interface OrderReservationService {
    void reserve(FlashSaleCreateOrderEvent event);

    void release(FlashSaleCreateOrderEvent event);
}
