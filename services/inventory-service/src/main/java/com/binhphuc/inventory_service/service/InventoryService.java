package com.binhphuc.inventory_service.service;

import com.binhphuc.inventory_service.dto.request.CreateProductStockRequest;
import com.binhphuc.inventory_service.dto.request.GetStockByVariantIdsRequest;
import com.binhphuc.inventory_service.dto.response.GetStockByVariantIdsResponse;

import java.util.List;

public interface InventoryService {
    void createProductStock(CreateProductStockRequest request);
    List<GetStockByVariantIdsResponse> getStockByVariantId(GetStockByVariantIdsRequest variantId);
}
