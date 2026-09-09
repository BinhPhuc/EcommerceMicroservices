package com.binhphuc.flash_sale_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.flash_sale_service.constant.OrderOutboxConstant;
import com.binhphuc.flash_sale_service.constant.StockConstant;
import com.binhphuc.flash_sale_service.kafka.event.FlashSaleOrderCreatedEvent;
import com.binhphuc.flash_sale_service.kafka.event.dto.FlashSaleOrderItem;
import com.binhphuc.flash_sale_service.publisher.impl.SoldOutPublisher;
import com.binhphuc.flash_sale_service.schedule.dto.SoldStatus;
import com.binhphuc.flash_sale_service.service.OrderReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderReservationServiceImpl implements OrderReservationService {
    private final StringRedisTemplate stringRedisTemplate;
    private final SoldOutPublisher soldOutPublisher;
    private final ObjectMapper objectMapper;
    @Qualifier("reserveStockScript")
    private final RedisScript<List> reserveStockScript;
    @Qualifier("releaseStockScript")
    private final RedisScript<List> releaseStockScript;

    @Override
    public void reserve(FlashSaleOrderCreatedEvent event) {
        List<FlashSaleOrderItem> items = event.getItems();
        List<String> keys = new ArrayList<>();
        keys.add(OrderOutboxConstant.ORDER_REQUEST_CACHE_KEY_PREFIX + event.getRequestId());
        keys.add(OrderOutboxConstant.OUTBOX_STREAM_KEY);
        items.forEach(item -> keys.add(buildStockCacheKey(event.getCampaignId(),
                item.getVariantId())));
        List<Object> args = new ArrayList<>();
        args.add(objectMapper.writeValueAsString(event));
        args.add(String.valueOf(OrderOutboxConstant.ORDER_REQUEST_TTL_SECONDS));
        args.add(String.valueOf(OrderOutboxConstant.OUTBOX_MAX_LENGTH));
        args.add(OrderOutboxConstant.OUTBOX_PAYLOAD_FIELD);
        items.forEach(item -> args.add(String.valueOf(item.getQuantity())));
        List<Long> result = stringRedisTemplate.execute(reserveStockScript, keys, args.toArray());
        if (result == null || result.isEmpty()) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Reserve stock script returned no result");
        }
        long status = result.get(0);
        if (status == StockConstant.RESERVE_STOCK_DUPLICATED_REQUEST) {
            log.info("Request id: {} is already reserved, skipping", event.getRequestId());
            return;
        }
        if (status != StockConstant.RESERVE_STOCK_SUCCESS) {
            String variantId = items.get(result.get(1).intValue() - 1).getVariantId();
            if (status == StockConstant.RESERVE_STOCK_KEY_NOT_FOUND) {
                throw new BusinessException(HttpStatus.CONFLICT,
                        "Stock is not pre-warmed for variant id: " + variantId);
            }
            if (status == StockConstant.RESERVE_STOCK_NOT_ENOUGH) {
                throw new BusinessException(HttpStatus.CONFLICT,
                        "Not enough stock for variant id: " + variantId);
            }
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Invalid cached stock value for variant id: " + variantId);
        }
        publishSoldOutItems(event, result);
    }

    @Override
    public void release(FlashSaleOrderCreatedEvent event) {
        List<FlashSaleOrderItem> items = event.getItems();
        List<String> keys = items.stream()
                .map(item -> buildStockCacheKey(event.getCampaignId(), item.getVariantId()))
                .toList();
        Object[] args = items.stream()
                .map(item -> String.valueOf(item.getQuantity()))
                .toArray();
        List<Long> result = stringRedisTemplate.execute(releaseStockScript, keys, args);
        if (result == null || result.size() != items.size()) {
            log.error("Failed to release stock of request id: {}, unexpected script result: {}",
                    event.getRequestId(), result);
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            FlashSaleOrderItem item = items.get(i);
            long remainingStock = result.get(i);
            if (remainingStock == StockConstant.RELEASE_STOCK_KEY_NOT_FOUND) {
                log.error("Cannot release {} stock of variant id: {}, cache key is gone",
                        item.getQuantity(), item.getVariantId());
            } else if (remainingStock == StockConstant.RELEASE_STOCK_INVALID_VALUE) {
                log.error("Cannot release {} stock of variant id: {}, cached value is not a number",
                        item.getQuantity(), item.getVariantId());
            } else {
                log.info("Released {} stock of variant id: {}, remaining stock: {}",
                        item.getQuantity(), item.getVariantId(), remainingStock);
            }
        }
    }

    private void publishSoldOutItems(FlashSaleOrderCreatedEvent event, List<Long> result) {
        List<FlashSaleOrderItem> items = event.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (result.get(i + 1) != 0L) {
                continue;
            }
            String variantId = items.get(i).getVariantId();
            log.info("Variant id: {} of campaign id: {} is sold out", variantId,
                    event.getCampaignId());
            soldOutPublisher.publish(SoldStatus
                    .builder()
                    .variantId(variantId)
                    .soldOut(true)
                    .build());
        }
    }

    private String buildStockCacheKey(String campaignId, String variantId) {
        return StockConstant.STOCK_CACHE_KEY_PREFIX + campaignId + ":" + variantId;
    }
}
