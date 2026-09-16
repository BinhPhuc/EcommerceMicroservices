package com.binhphuc.inventory_service.service.impl;

import com.binhphuc.inventory_service.entity.ProcessedEvent;
import com.binhphuc.inventory_service.kafka.command.CacheStockCommand;
import com.binhphuc.inventory_service.kafka.event.dto.FlashSaleItem;
import com.binhphuc.inventory_service.repository.ProcessedEventRepository;
import com.binhphuc.inventory_service.service.InventoryService;
import com.binhphuc.inventory_service.service.PreWarmItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreWarmItemServiceImpl implements PreWarmItemService {
    private final InventoryService inventoryService;
    private final ProcessedEventRepository processedEventRepository;
    @Qualifier("flashSaleRedisCacheManager")
    private final RedisCacheManager redisCacheManager;
    private static final String CACHE_NAME = "stock";

    @Override
    @Transactional
    public void preWarmItem(CacheStockCommand command) {
        if (processedEventRepository.existsByIdempotencyKey(command.getCampaignId())) {
            return;
        }
        processedEventRepository.saveAndFlush(ProcessedEvent.builder().idempotencyKey(command.getCampaignId()).build());
        String campaignId = command.getCampaignId();
        List<FlashSaleItem> flashSaleItems = command.getFlashSaleItems();
        Cache cache = redisCacheManager.getCache(CACHE_NAME);
        flashSaleItems.forEach(item -> {
            String variantId = item.getVariantId();
            Long reserveStock = item.getStock();
            cache.putIfAbsent(getCacheKey(campaignId, variantId), reserveStock);
            inventoryService.updateInventoryReserveStock(variantId, reserveStock);
        });
    }

    private String getCacheKey(String campaignId, String variantId) {
        return new StringBuilder(campaignId).append(":").append(variantId).toString();
    }
}
