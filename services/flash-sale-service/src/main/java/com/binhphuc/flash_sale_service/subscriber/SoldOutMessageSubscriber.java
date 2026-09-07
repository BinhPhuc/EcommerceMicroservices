package com.binhphuc.flash_sale_service.subscriber;

import com.binhphuc.flash_sale_service.constant.PreWarmItemConstant;
import com.binhphuc.flash_sale_service.helper.CacheHelper;
import com.binhphuc.flash_sale_service.schedule.dto.SoldStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class SoldOutMessageSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    @Qualifier("caffeineCacheManager")
    private final CacheManager cacheManager;

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        SoldStatus soldStatus = parseMessage(message.getBody().toString());
        if (soldStatus == null) {
            log.error("Received null SoldStatus from message: {}", message);
            return;
        }
        log.info("Received sold out message: {}", soldStatus);
        if (soldStatus.isSoldOut()) {
            Cache cache = cacheManager.getCache(PreWarmItemConstant.SOLD_STATUS_CACHE_NAME);
            String cacheKey =
                    CacheHelper.createCacheKey(PreWarmItemConstant.SOLD_STATUS_PRIMARY_CACHE_KEY,
                            List.of(soldStatus.getVariantId()));
            cache.put(cacheKey, soldStatus);
        }
    }

    private SoldStatus parseMessage(String message) {
        try {
            return objectMapper.readValue(message, SoldStatus.class);
        } catch (Exception e) {
            log.error("Failed to parse message: {}", message, e);
        }
        return null;
    }
}
