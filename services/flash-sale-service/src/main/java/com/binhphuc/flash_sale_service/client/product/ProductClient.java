package com.binhphuc.flash_sale_service.client.product;

import com.binhphuc.flash_sale_service.client.product.dto.request.GetFlashSaleItemRequest;
import com.binhphuc.flash_sale_service.client.product.dto.response.GetProductResponse;

import java.util.List;

public interface ProductClient {
    List<GetProductResponse> getFlashSaleItems(GetFlashSaleItemRequest getFlashSaleItemRequest);
}
