package com.binhphuc.product_service.service;

import java.util.List;

import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.request.GetProductByIdsRequest;
import com.binhphuc.product_service.dto.product.request.UpdateProductStockRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;
import com.binhphuc.product_service.dto.product.response.GetProductByIdsResponse;

public interface ProductService {
    CreateProductResponse create(CreateProductRequest productRequest);

    List<GetProductByIdsResponse> getProductByIds(GetProductByIdsRequest getProductByIdsRequest);

    void updateStock(UpdateProductStockRequest updateStockRequest);
}
