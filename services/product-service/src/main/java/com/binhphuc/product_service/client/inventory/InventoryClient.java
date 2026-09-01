package com.binhphuc.product_service.client.inventory;

import com.binhphuc.product_service.client.inventory.dto.request.CreateProductStockRequest;

public interface InventoryClient {
    void createProductStock(CreateProductStockRequest request);
}
