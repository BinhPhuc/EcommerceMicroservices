package com.binhphuc.inventory_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.inventory_service.dto.request.CreateProductStockRequest;
import com.binhphuc.inventory_service.dto.request.GetStockByVariantIdsRequest;
import com.binhphuc.inventory_service.dto.response.GetStockByVariantIdsResponse;
import com.binhphuc.inventory_service.entity.Inventory;
import com.binhphuc.inventory_service.repository.InventoryRepository;
import com.binhphuc.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    @Qualifier("flashSaleRedisCacheManager")
    private final RedisCacheManager redisCacheManager;
    private static final String CACHE_NAME = "stock";

    @Override
    public void createProductStock(CreateProductStockRequest request) {
        Inventory inventory = Inventory
                .builder()
                .stock(request.getStock())
                .variantId(request.getVariantId())
                .build();
        inventoryRepository.save(inventory);
    }

    @Override
    public List<GetStockByVariantIdsResponse> getStockByVariantId(GetStockByVariantIdsRequest request) {
        String campaignId = request.getCampaignId();
        List<String> variantIds = request.getVariantIds();
        Cache cache = redisCacheManager.getCache(CACHE_NAME);
        List<GetStockByVariantIdsResponse> response = new ArrayList<>();
        variantIds.forEach(variantId -> {
            String cacheKey = new StringBuilder(campaignId).append(":").append(variantId).toString();
            Long stock = cache.get(cacheKey, Long.class);
            if (stock == null) {
                Inventory inventory =
                        inventoryRepository.findByVariantId(variantId).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Variant ID " + variantId + " not found in inventory"));
                stock = inventory.getStock();
                cache.put(cacheKey, stock);
            }
            response.add(GetStockByVariantIdsResponse.builder().variantId(variantId).stock(stock).build());
        });
        return response;
    }
}
