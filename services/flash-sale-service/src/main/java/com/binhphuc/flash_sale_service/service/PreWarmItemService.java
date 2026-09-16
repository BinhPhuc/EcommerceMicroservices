package com.binhphuc.flash_sale_service.service;

import com.binhphuc.flash_sale_service.kafka.event.dto.FlashSaleItem;
import org.quartz.SchedulerException;

import java.time.Instant;
import java.util.List;

public interface PreWarmItemService {
    void preWarmItem(Instant startTime, List<FlashSaleItem> flashSaleItems, String campaignId) throws SchedulerException;
}
