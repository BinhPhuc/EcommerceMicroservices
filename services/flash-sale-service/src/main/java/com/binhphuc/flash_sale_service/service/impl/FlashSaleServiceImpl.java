package com.binhphuc.flash_sale_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.flash_sale_service.client.inventory.InventoryClient;
import com.binhphuc.flash_sale_service.client.inventory.dto.request.GetStockByVariantIdsRequest;
import com.binhphuc.flash_sale_service.client.inventory.dto.response.GetStockByVariantIdsResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateCampaignItemRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateCampaignRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.CreateCampaignResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.GetCampaignItemResponse;
import com.binhphuc.flash_sale_service.entity.Campaign;
import com.binhphuc.flash_sale_service.entity.CampaignItem;
import com.binhphuc.flash_sale_service.repository.CampaignItemRepository;
import com.binhphuc.flash_sale_service.repository.CampaignRepository;
import com.binhphuc.flash_sale_service.service.FlashSaleService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.binhphuc.flash_sale_service.service.PreWarmItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.quartz.SchedulerException;
import org.redisson.api.RedissonClient;
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
    private final PreWarmItemService preWarmItemService;

    @Override
    @Transactional
    public CreateCampaignResponse createCampaign(CreateCampaignRequest createCampaignRequest) {
        Instant startedAt = createCampaignRequest.getStartedAt();
        Instant endedAt = createCampaignRequest.getEndedAt();
        List<String> productIds = createCampaignRequest.getItems().stream().map(CreateCampaignItemRequest::getProductId).toList();
        if (!startedAt.isBefore(endedAt)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Campaign start time must be before end time");
        }
        Set<String> variantIds = new HashSet<>();
        for (CreateCampaignItemRequest itemRequest : createCampaignRequest.getItems()) {
            if (!variantIds.add(itemRequest.getVariantId())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Duplicated campaign item with variant id: " +
                        itemRequest.getVariantId());
            }
        }
        List<String> variantIdsList = createCampaignRequest.getItems().stream()
                .map(CreateCampaignItemRequest::getVariantId)
                .toList();
        Map<String, Long> variantIdToStock = new HashMap<>();
        createCampaignRequest.getItems().forEach(itemRequest -> variantIdToStock.put(itemRequest.getVariantId(), itemRequest.getStock()));
        List<GetStockByVariantIdsResponse> stockResponse = inventoryClient.getStockByVariantIds(GetStockByVariantIdsRequest.builder().variantIds(variantIdsList).build());
        stockResponse.forEach(stock -> {
            if (stock.getStock() < variantIdToStock.get(stock.getVariantId())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Not enough stock for variant id: " + stock.getVariantId());
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
        List<CampaignItem> campaignItemList = createCampaignRequest.getItems().stream().map(itemRequest -> {
            CampaignItem newCampaignItem = CampaignItem
                    .builder()
                    .campaignId(savedCampaign.getId())
                    .productId(itemRequest.getProductId())
                    .variantId(itemRequest.getVariantId())
                    .price(itemRequest.getPrice())
                    .stock(itemRequest.getStock())
                    .soldQuantity(0L)
                    .build();
            return newCampaignItem;
        }).toList();
        campaignItemRepository.saveAll(campaignItemList);
        Instant startJobTime = startedAt.minus(15, ChronoUnit.MINUTES);
        try {
            preWarmItemService.preWarmItem(startJobTime, productIds, variantIdsList);
        } catch (SchedulerException e) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to schedule pre-warm item job: " + e.getMessage());
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
            throw new BusinessException(HttpStatus.NOT_FOUND, "Campaign not found with id: " + campaignId);
        }
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
}
