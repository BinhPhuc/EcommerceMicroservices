package com.binhphuc.flash_sale_service.service;

import org.quartz.SchedulerException;

import java.time.Instant;
import java.util.List;

public interface PreWarmItemService {
    void preWarmItem(Instant startTime, List<String> productIds, List<String> variantIds) throws SchedulerException;
}
