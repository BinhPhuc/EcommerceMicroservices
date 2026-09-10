package com.binhphuc.flash_sale_service.service.impl;

import com.binhphuc.flash_sale_service.constant.OrderOutboxConstant;
import com.binhphuc.flash_sale_service.kafka.event.FlashSaleCreateOrderEvent;
import com.binhphuc.flash_sale_service.kafka.producer.FlashSaleOrderProducer;
import com.binhphuc.flash_sale_service.service.OrderOutboxService;
import com.binhphuc.flash_sale_service.service.OrderReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderOutboxServiceImpl implements OrderOutboxService {
    private final StringRedisTemplate stringRedisTemplate;
    private final FlashSaleOrderProducer flashSaleOrderProducer;
    private final OrderReservationService orderReservationService;
    private final ObjectMapper objectMapper;
    @Qualifier("outboxConsumerName")
    private final String outboxConsumerName;

    @Override
    public void createConsumerGroup() {
        try {
            streamOperations().createGroup(OrderOutboxConstant.OUTBOX_STREAM_KEY,
                    ReadOffset.from("0"), OrderOutboxConstant.OUTBOX_CONSUMER_GROUP);
        } catch (DataAccessException e) {
            log.info("Consumer group {} already exists on stream {}",
                    OrderOutboxConstant.OUTBOX_CONSUMER_GROUP,
                    OrderOutboxConstant.OUTBOX_STREAM_KEY);
        }
    }

    @Override
    public void relay(MapRecord<String, String, String> record) {
        FlashSaleCreateOrderEvent event = parseRecord(record);
        if (event == null) {
            acknowledge(record.getId());
            return;
        }
        try {
            flashSaleOrderProducer.sendFlashSaleOrderCreatedEvent(event)
                    .get(OrderOutboxConstant.OUTBOX_SEND_TIMEOUT.toMillis(),
                            TimeUnit.MILLISECONDS);
            acknowledge(record.getId());
            log.info("Relayed outbox record id: {} of request id: {}", record.getId(),
                    event.getRequestId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while relaying outbox record id: {}", record.getId(), e);
        } catch (Exception e) {
            log.error("Failed to relay outbox record id: {} of request id: {}", record.getId(),
                    event.getRequestId(), e);
        }
    }

    @Override
    public void recoverPendingRecords() {
        PendingMessages pendingMessages = streamOperations().pending(
                OrderOutboxConstant.OUTBOX_STREAM_KEY, OrderOutboxConstant.OUTBOX_CONSUMER_GROUP,
                Range.unbounded(), OrderOutboxConstant.RECOVERY_BATCH_SIZE);
        if (pendingMessages == null || pendingMessages.isEmpty()) {
            return;
        }
        for (PendingMessage pendingMessage : pendingMessages) {
            if (pendingMessage.getElapsedTimeSinceLastDelivery()
                    .compareTo(OrderOutboxConstant.MIN_IDLE_TIME_BEFORE_RETRY) < 0) {
                continue;
            }
            if (pendingMessage.getTotalDeliveryCount() >= OrderOutboxConstant.MAX_DELIVERY_ATTEMPTS) {
                discard(pendingMessage.getId());
                continue;
            }
            claimAndRelay(pendingMessage.getId());
        }
    }

    private void claimAndRelay(RecordId recordId) {
        List<MapRecord<String, String, String>> claimedRecords =
                streamOperations().claim(OrderOutboxConstant.OUTBOX_STREAM_KEY,
                        OrderOutboxConstant.OUTBOX_CONSUMER_GROUP, outboxConsumerName,
                        OrderOutboxConstant.MIN_IDLE_TIME_BEFORE_RETRY, recordId);
        if (claimedRecords == null || claimedRecords.isEmpty()) {
            return;
        }
        claimedRecords.forEach(this::relay);
    }

    private void discard(RecordId recordId) {
        List<MapRecord<String, String, String>> records =
                streamOperations().range(OrderOutboxConstant.OUTBOX_STREAM_KEY,
                        Range.closed(recordId.getValue(), recordId.getValue()));
        if (records == null || records.isEmpty()) {
            acknowledge(recordId);
            return;
        }
        FlashSaleCreateOrderEvent event = parseRecord(records.get(0));
        if (event != null) {
            log.error("Outbox record id: {} of request id: {} exhausted {} delivery attempts, " +
                            "releasing reserved stock", recordId, event.getRequestId(),
                    OrderOutboxConstant.MAX_DELIVERY_ATTEMPTS);
            orderReservationService.release(event);
        }
        acknowledge(recordId);
    }

    private void acknowledge(RecordId recordId) {
        streamOperations().acknowledge(OrderOutboxConstant.OUTBOX_STREAM_KEY,
                OrderOutboxConstant.OUTBOX_CONSUMER_GROUP, recordId);
    }

    private StreamOperations<String, String, String> streamOperations() {
        return stringRedisTemplate.opsForStream();
    }

    private FlashSaleCreateOrderEvent parseRecord(MapRecord<String, String, String> record) {
        String payload = record.getValue().get(OrderOutboxConstant.OUTBOX_PAYLOAD_FIELD);
        try {
            return objectMapper.readValue(payload, FlashSaleCreateOrderEvent.class);
        } catch (Exception e) {
            log.error("Failed to parse outbox record id: {}, payload: {}", record.getId(), payload
                    , e);
        }
        return null;
    }
}
