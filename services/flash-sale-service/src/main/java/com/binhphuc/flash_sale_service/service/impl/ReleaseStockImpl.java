package com.binhphuc.flash_sale_service.service.impl;

import com.binhphuc.flash_sale_service.constant.StockConstant;
import com.binhphuc.flash_sale_service.entity.ProcessedEvent;
import com.binhphuc.flash_sale_service.kafka.command.ReleaseStockCommand;
import com.binhphuc.flash_sale_service.kafka.event.dto.StockItem;
import com.binhphuc.flash_sale_service.publisher.impl.SoldOutPublisher;
import com.binhphuc.flash_sale_service.repository.ProcessedEventRepository;
import com.binhphuc.flash_sale_service.schedule.dto.SoldStatus;
import com.binhphuc.flash_sale_service.service.ReleaseStockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReleaseStockImpl implements ReleaseStockService {
    private final StringRedisTemplate stringRedisTemplate;
    private final ProcessedEventRepository processedEventRepository;
    private final SoldOutPublisher soldOutPublisher;
    @Qualifier("releaseStockScript")
    private final RedisScript<List> releaseStockScript;

    @Override
    public void releaseStock(ReleaseStockCommand command) {
        String idempotencyKey = command.getIdempotencyKey();
        List<StockItem> items = command.getItems();
        if (items == null || items.isEmpty()) {
            log.warn("Release stock of idempotency key: {} has no item, skipping", idempotencyKey);
            return;
        }
        if (!claimEvent(idempotencyKey)) {
            return;
        }
        List<String> keys = items.stream()
                .map(item -> buildStockCacheKey(command.getCampaignId(), item.getVariantId()))
                .toList();
        Object[] args = items.stream()
                .map(item -> String.valueOf(item.getQuantity()))
                .toArray();
        List<Long> result = stringRedisTemplate.execute(releaseStockScript, keys, args);
        if (result == null || result.size() != items.size()) {
            log.error("Failed to release stock of idempotency key: {}, unexpected script result: {}",
                    idempotencyKey, result);
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            handleReleasedItem(command, items.get(i), result.get(i));
        }
    }

    private boolean claimEvent(String idempotencyKey) {
        if (processedEventRepository.existsByIdempotencyKey(idempotencyKey)) {
            log.info("Idempotency key: {} is already released, skipping", idempotencyKey);
            return false;
        }
        try {
            processedEventRepository.saveAndFlush(ProcessedEvent.builder()
                    .idempotencyKey(idempotencyKey)
                    .build());
            return true;
        } catch (DataIntegrityViolationException e) {
            log.info("Idempotency key: {} is being released concurrently, skipping", idempotencyKey);
            return false;
        }
    }

    private void handleReleasedItem(ReleaseStockCommand command, StockItem item,
                                    long remainingStock) {
        if (remainingStock == StockConstant.RELEASE_STOCK_KEY_NOT_FOUND) {
            log.error("Cannot release {} stock of variant id: {}, cache key is gone",
                    item.getQuantity(), item.getVariantId());
            return;
        }
        if (remainingStock == StockConstant.RELEASE_STOCK_INVALID_VALUE) {
            log.error("Cannot release {} stock of variant id: {}, cached value is not a number",
                    item.getQuantity(), item.getVariantId());
            return;
        }
        log.info("Released {} stock of variant id: {} of campaign id: {}, remaining stock: {}",
                item.getQuantity(), item.getVariantId(), command.getCampaignId(), remainingStock);
        soldOutPublisher.publish(SoldStatus.builder()
                .variantId(item.getVariantId())
                .soldOut(false)
                .build());
    }

    private String buildStockCacheKey(String campaignId, String variantId) {
        return StockConstant.STOCK_CACHE_KEY_PREFIX + campaignId + ":" + variantId;
    }
}
