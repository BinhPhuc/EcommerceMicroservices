package com.binhphuc.product_service.service;

import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;

public interface ProductService {
  CreateProductResponse create(CreateProductRequest productRequest);
}
