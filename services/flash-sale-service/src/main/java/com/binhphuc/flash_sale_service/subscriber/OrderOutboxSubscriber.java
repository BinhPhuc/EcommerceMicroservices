package com.binhphuc.flash_sale_service.subscriber;

import com.binhphuc.flash_sale_service.service.OrderOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderOutboxSubscriber implements StreamListener<String, MapRecord<String, String,
        String>> {
    private final OrderOutboxService orderOutboxService;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        orderOutboxService.relay(record);
    }
}
