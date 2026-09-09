package com.binhphuc.flash_sale_service.service;

import org.springframework.data.redis.connection.stream.MapRecord;

public interface OrderOutboxService {
    void relay(MapRecord<String, String, String> record);

    void recoverPendingRecords();

    void createConsumerGroup();
}
