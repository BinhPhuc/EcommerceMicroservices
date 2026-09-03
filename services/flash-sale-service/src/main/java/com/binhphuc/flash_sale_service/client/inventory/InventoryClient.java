package com.binhphuc.flash_sale_service.client.inventory;

import com.binhphuc.flash_sale_service.client.inventory.dto.request.GetStockByVariantIdsRequest;
import com.binhphuc.flash_sale_service.client.inventory.dto.response.GetStockByVariantIdsResponse;

import java.util.List;

public interface InventoryClient {
    List<GetStockByVariantIdsResponse> getStockByVariantIds(GetStockByVariantIdsRequest request);
}
