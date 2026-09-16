package com.binhphuc.flash_sale_service.service;

import com.binhphuc.flash_sale_service.kafka.command.ReleaseStockCommand;

public interface ReleaseStockService {
    void releaseStock(ReleaseStockCommand command);
}
