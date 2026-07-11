package com.binhphuc.product_service.service;

import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.entity.Product;

public interface IProductService {
  Product create(CreateProductRequest productRequest);
}
