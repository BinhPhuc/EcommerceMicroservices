package com.binhphuc.product_service.service;

import com.binhphuc.product_service.kafka.command.PreWarmItemCommand;

public interface PreWarmItemService {
    void preWarmItem(PreWarmItemCommand command);
}
