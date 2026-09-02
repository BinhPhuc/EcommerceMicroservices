package com.binhphuc.inventory_service.service;

import com.binhphuc.inventory_service.dto.request.CreateProductStockRequest;
import com.binhphuc.inventory_service.dto.request.GetStockByVariantIdRequest;
import com.binhphuc.inventory_service.dto.response.GetStockByVariantIdResponse;

import java.util.List;

public interface InventoryService {
    void createProductStock(CreateProductStockRequest request);
    List<GetStockByVariantIdResponse> getStockByVariantId(GetStockByVariantIdRequest variantId);
}
