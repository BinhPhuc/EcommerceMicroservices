package com.binhphuc.product_service.service.impl;

import com.binhphuc.product_service.dto.product.request.GetFlashSaleItemRequest;
import com.binhphuc.product_service.kafka.command.PreWarmItemCommand;
import com.binhphuc.product_service.kafka.event.dto.flashsale.FlashSaleItem;
import com.binhphuc.product_service.service.PreWarmItemService;
import com.binhphuc.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreWarmItemImpl implements PreWarmItemService {
    private final ProductService productService;

    @Override
    public void preWarmItem(PreWarmItemCommand command) {
        List<FlashSaleItem> flashSaleItems = command.getFlashSaleItems();
        String campaignId = command.getCampaignId();
        productService.getFlashSaleItems(GetFlashSaleItemRequest
                .builder()
                .flashSaleItems(flashSaleItems.stream()
                        .map(item -> com.binhphuc.product_service.dto.falshsale.FlashSaleItem.builder()
                                .productId(item.getProductId())
                                .variantId(item.getVariantId())
                                .build())
                        .toList())
                .campaignId(campaignId)
                .build());
    }
}
