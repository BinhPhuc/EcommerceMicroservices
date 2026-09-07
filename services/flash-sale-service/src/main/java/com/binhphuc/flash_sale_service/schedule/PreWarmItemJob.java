package com.binhphuc.flash_sale_service.schedule;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.flash_sale_service.constant.PreWarmItemConstant;
import com.binhphuc.flash_sale_service.helper.CacheHelper;
import com.binhphuc.flash_sale_service.kafka.event.dto.FlashSaleItem;
import com.binhphuc.flash_sale_service.kafka.event.PreWarmItemEvent;
import com.binhphuc.flash_sale_service.kafka.producer.PreWarmItemProducer;
import com.binhphuc.flash_sale_service.schedule.dto.SoldStatus;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PreWarmItemJob implements Job {
    private final PreWarmItemProducer preWarmItemProducer;
    @Qualifier("caffeineCacheManager")
    private final CacheManager cacheManager;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<FlashSaleItem> flashSaleItems =
                (List<FlashSaleItem>) jobExecutionContext.getJobDetail().getJobDataMap().get(PreWarmItemConstant.FLASH_SALE_ITEMS_KEY);
        String campaignId =
                (String) jobExecutionContext.getJobDetail().getJobDataMap().get(PreWarmItemConstant.CAMPAIGN_ID_KEY);
        if (flashSaleItems == null) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Flash sale items is " +
                    "null");
        }
        cacheSoldStatus(flashSaleItems);
        preWarmItemProducer.sendPreWarmItemEvent(PreWarmItemEvent.builder().flashSaleItems(flashSaleItems).campaignId(campaignId).build());
    }

    private void cacheSoldStatus(List<FlashSaleItem> flashSaleItems) {
        Cache cache = cacheManager.getCache(PreWarmItemConstant.SOLD_STATUS_CACHE_NAME);
        flashSaleItems.forEach(item -> {
            SoldStatus soldStatus = SoldStatus
                    .builder()
                    .variantId(item.getVariantId())
                    .soldOut(false)
                    .build();
            String cacheKey = CacheHelper.createCacheKey(PreWarmItemConstant.SOLD_STATUS_PRIMARY_CACHE_KEY,
                    List.of(item.getVariantId()));
            cache.put(cacheKey, soldStatus);
        });
    }
}
