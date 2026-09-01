package com.binhphuc.flash_sale_service.service;

import java.util.List;

import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateFlashSaleRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.ReserveFlashSaleItemRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.CreateFlashSaleResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.GetFlashSaleItemResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.ReserveFlashSaleItemResponse;

public interface FlashSaleService {
    CreateFlashSaleResponse create(CreateFlashSaleRequest createFlashSaleRequest);

    List<GetFlashSaleItemResponse> getItemsByFlashSaleId(String flashSaleId);

    ReserveFlashSaleItemResponse reserveItem(ReserveFlashSaleItemRequest reserveFlashSaleItemRequest);
}
