package com.binhphuc.inventory_service.service.impl;

import com.binhphuc.inventory_service.entity.Inventory;
import com.binhphuc.inventory_service.kafka.command.CacheStockCommand;
import com.binhphuc.inventory_service.kafka.event.dto.FlashSaleItem;
import com.binhphuc.inventory_service.repository.InventoryRepository;
import com.binhphuc.inventory_service.service.InventoryService;
import com.binhphuc.inventory_service.service.PreWarmItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreWarmItemServiceImpl implements PreWarmItemService {
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    @Qualifier("flashSaleRedisCacheManager")
    private final RedisCacheManager redisCacheManager;
    private static final String CACHE_NAME = "stock";

    @Override
    public void preWarmItem(CacheStockCommand command) {
        String campaignId = command.getCampaignId();
        List<FlashSaleItem> flashSaleItems = command.getFlashSaleItems();
        Cache cache = redisCacheManager.getCache(CACHE_NAME);
        flashSaleItems.forEach(item -> {
            String variantId = item.getVariantId();
            Long reserveStock = item.getStock();
            cache.put(getCacheKey(campaignId, variantId), reserveStock);
            Inventory inventory = inventoryService.getInventoryByVariantId(variantId);
            inventoryRepository.save(inventory);
        });
    }

    private String getCacheKey(String campaignId, String variantId) {
        return new StringBuilder(campaignId).append(":").append(variantId).toString();
    }
}
