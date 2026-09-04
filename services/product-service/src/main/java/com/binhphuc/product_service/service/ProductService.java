package com.binhphuc.product_service.service;

import java.util.List;

import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.request.GetFlashSaleItemRequest;
import com.binhphuc.product_service.dto.product.request.GetProductByIdsRequest;
import com.binhphuc.product_service.dto.product.response.GetProductResponse;
import com.binhphuc.product_service.kafka.command.LockProductStockCommand;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;

public interface ProductService {
    CreateProductResponse create(CreateProductRequest productRequest);

    List<GetProductResponse> getProductByIds(GetProductByIdsRequest getProductByIdsRequest);

    void lockProductStock(LockProductStockCommand lockProductStockCommand);

    List<GetProductResponse> getFlashSaleItems(GetFlashSaleItemRequest getFlashSaleItemRequest);
}
