package com.binhphuc.product_service.service;

import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;

public interface IProductService {
  CreateProductResponse create(CreateProductRequest productRequest);
}
