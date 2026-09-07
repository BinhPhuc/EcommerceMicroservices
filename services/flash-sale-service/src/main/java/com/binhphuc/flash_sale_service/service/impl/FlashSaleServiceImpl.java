package com.binhphuc.flash_sale_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.flash_sale_service.client.inventory.InventoryClient;
import com.binhphuc.flash_sale_service.client.inventory.dto.request.GetStockByVariantIdsRequest;
import com.binhphuc.flash_sale_service.client.inventory.dto.response.GetStockByVariantIdsResponse;
import com.binhphuc.flash_sale_service.client.product.ProductClient;
import com.binhphuc.flash_sale_service.client.product.dto.request.GetFlashSaleItemRequest;
import com.binhphuc.flash_sale_service.constant.PreWarmItemConstant;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateCampaignItemRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateCampaignRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.CreateCampaignResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.GetCampaignItemResponse;
import com.binhphuc.flash_sale_service.dto.order.request.CreateOrderRequest;
import com.binhphuc.flash_sale_service.dto.order.request.OrderItem;
import com.binhphuc.flash_sale_service.entity.Campaign;
import com.binhphuc.flash_sale_service.entity.CampaignItem;
import com.binhphuc.flash_sale_service.helper.CacheHelper;
import com.binhphuc.flash_sale_service.kafka.event.dto.FlashSaleItem;
import com.binhphuc.flash_sale_service.repository.CampaignItemRepository;
import com.binhphuc.flash_sale_service.repository.CampaignRepository;
import com.binhphuc.flash_sale_service.schedule.dto.SoldStatus;
import com.binhphuc.flash_sale_service.service.FlashSaleService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.binhphuc.flash_sale_service.service.PreWarmItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.quartz.SchedulerException;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleServiceImpl implements FlashSaleService {
    private final CampaignRepository campaignRepository;
    private final CampaignItemRepository campaignItemRepository;
    private final RedissonClient redissonClient;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;
    private final PreWarmItemService preWarmItemService;
    @Qualifier("caffeineCacheManager")
    private final CacheManager cacheManger;

    @Override
    @Transactional
    public CreateCampaignResponse createCampaign(CreateCampaignRequest createCampaignRequest) {
        Instant startedAt = createCampaignRequest.getStartedAt();
        Instant endedAt = createCampaignRequest.getEndedAt();
        if (!startedAt.isBefore(endedAt)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Campaign start time must be " +
                    "before end time");
        }
        Set<String> variantIds = new HashSet<>();
        for (CreateCampaignItemRequest itemRequest : createCampaignRequest.getItems()) {
            if (!variantIds.add(itemRequest.getVariantId())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Duplicated campaign item " +
                        "with variant id: " +
                        itemRequest.getVariantId());
            }
        }
        List<String> variantIdsList = createCampaignRequest.getItems().stream()
                .map(CreateCampaignItemRequest::getVariantId)
                .toList();
        Map<String, Long> variantIdToStock = new HashMap<>();
        createCampaignRequest.getItems().forEach(itemRequest -> variantIdToStock.put(itemRequest.getVariantId(),
                itemRequest.getStock()));
        List<GetStockByVariantIdsResponse> stockResponse =
                inventoryClient.getStockByVariantIds(GetStockByVariantIdsRequest.builder().variantIds(variantIdsList).build());
        stockResponse.forEach(stock -> {
            if (stock.getStock() < variantIdToStock.get(stock.getVariantId())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Not enough stock for " +
                        "variant id: " + stock.getVariantId());
            }
        });
        Campaign newCampaign = Campaign
                .builder()
                .name(createCampaignRequest.getName())
                .description(createCampaignRequest.getDescription())
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
        Campaign savedCampaign = campaignRepository.save(newCampaign);
        String campaignId = savedCampaign.getId();
        List<CampaignItem> campaignItemList =
                createCampaignRequest.getItems().stream().map(itemRequest ->
                        CampaignItem
                                .builder()
                                .campaignId(campaignId)
                                .productId(itemRequest.getProductId())
                                .variantId(itemRequest.getVariantId())
                                .price(itemRequest.getPrice())
                                .stock(itemRequest.getStock())
                                .soldQuantity(0L)
                                .build()
                ).toList();
        campaignItemRepository.saveAll(campaignItemList);
        // TODO: for testing pre-warm item job, set startJobTime = Instant.now().plus(1,
        //  ChronoUnit.MINUTES);
        // Instant startJobTime = startedAt.minus(15, ChronoUnit.MINUTES);
        Instant startJobTime = Instant.now().plus(2, ChronoUnit.MINUTES);
        List<FlashSaleItem> flashSaleItems =
                createCampaignRequest.getItems().stream().map(itemRequest -> FlashSaleItem
                        .builder()
                        .productId(itemRequest.getProductId())
                        .variantId(itemRequest.getVariantId())
                        .build()).toList();
        try {
            preWarmItemService.preWarmItem(startJobTime, flashSaleItems, campaignId);
        } catch (SchedulerException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to schedule " +
                    "pre-warm item job: " + e.getMessage());
        }
        return CreateCampaignResponse
                .builder()
                .id(savedCampaign.getId())
                .name(savedCampaign.getName())
                .startedAt(savedCampaign.getStartedAt())
                .endedAt(savedCampaign.getEndedAt())
                .build();
    }

    @Override
    @Cacheable(value = "campaign-items", key = "#campaignId", condition = "#campaignId != null")
    public List<GetCampaignItemResponse> getItemsByCampaignId(String campaignId) {
        if (!campaignRepository.existsByIdAndIsDeletedFalse(campaignId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND,
                    "Campaign not found with id: " + campaignId);
        }
        List<CampaignItem> campaignItems =
                campaignItemRepository.findByCampaignIdAndIsDeletedFalse(campaignId);
        List<com.binhphuc.flash_sale_service.client.product.dto.request.FlashSaleItem> flashSaleItems =
                campaignItems.stream().map(campaignItem -> com.binhphuc.flash_sale_service.client.product.dto.request.FlashSaleItem
                        .builder()
                        .productId(campaignItem.getProductId())
                        .variantId(campaignItem.getVariantId())
                        .build()).toList();
        GetFlashSaleItemRequest request = GetFlashSaleItemRequest
                .builder()
                .flashSaleItems(flashSaleItems)
                .build();
        productClient.getFlashSaleItems(request);
        return campaignItemRepository
                .findByCampaignIdAndIsDeletedFalse(campaignId)
                .stream()
                .map(campaignItem -> GetCampaignItemResponse
                        .builder()
                        .id(campaignItem.getId())
                        .productId(campaignItem.getProductId())
                        .variantId(campaignItem.getVariantId())
                        .price(campaignItem.getPrice())
                        .stock(campaignItem.getStock())
                        .soldQuantity(campaignItem.getSoldQuantity())
                        .build())
                .toList();
    }

    @Override
    public void createOrder(CreateOrderRequest createOrderRequest, String campaignId) {
        List<CampaignItem> campaignItems =
                campaignItemRepository.findByCampaignIdAndIsDeletedFalse(campaignId);
        Map<String, Boolean> existedItems = new HashMap<>();
        campaignItems.forEach(item -> existedItems.put(item.getVariantId(), true));
        createOrderRequest.getItems().forEach(orderItem -> {
            if (!existedItems.containsKey(orderItem.getVariantId())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST,
                        "Variant id: " + orderItem.getVariantId() + " is not part of campaign " +
                                "id: " + campaignId);
            }
        });
        Cache cache = cacheManger.getCache(PreWarmItemConstant.SOLD_STATUS_CACHE_NAME);
        List<OrderItem> orderItems = createOrderRequest.getItems();
        orderItems.forEach(orderItem -> {
            String soldOutCacheKey =
                    CacheHelper.createCacheKey(PreWarmItemConstant.SOLD_STATUS_CACHE_KEY,
                            List.of(orderItem.getVariantId()));
            SoldStatus soldStatus = cache.get(soldOutCacheKey, SoldStatus.class);
            if (soldStatus.isSoldOut()) {
                throw new BusinessException(HttpStatus.CONFLICT,
                        "Variant id: " + orderItem.getVariantId() + " is sold out");
            }
        });
    }
}
