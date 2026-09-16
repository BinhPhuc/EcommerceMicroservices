package com.binhphuc.product_service.client.inventory;

import com.binhphuc.product_service.client.inventory.dto.request.CreateProductStockRequest;
import com.binhphuc.product_service.client.inventory.dto.request.GetStockByVariantIdsRequest;
import com.binhphuc.product_service.client.inventory.dto.response.GetStockByVariantIdsResponse;

import java.util.List;

public interface InventoryClient {
    void createProductStock(CreateProductStockRequest request);

    List<GetStockByVariantIdsResponse> getStock(GetStockByVariantIdsRequest request);
}
