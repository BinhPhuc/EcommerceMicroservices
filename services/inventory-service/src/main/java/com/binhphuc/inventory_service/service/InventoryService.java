package com.binhphuc.inventory_service.service;

import com.binhphuc.inventory_service.dto.request.CreateProductStockRequest;

public interface InventoryService {
    void createProductStock(CreateProductStockRequest request);
}
