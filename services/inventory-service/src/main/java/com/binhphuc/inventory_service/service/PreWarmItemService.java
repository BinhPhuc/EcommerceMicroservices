package com.binhphuc.inventory_service.service;

import com.binhphuc.inventory_service.kafka.command.CacheStockCommand;

public interface PreWarmItemService {
    void preWarmItem(CacheStockCommand command);
}
