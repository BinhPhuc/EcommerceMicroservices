package com.binhphuc.product_service.service;

import com.binhphuc.product_service.dto.product.request.CreateCategoryRequest;
import com.binhphuc.product_service.dto.product.response.CreateCategoryResponse;

public interface ICategoryService {
  CreateCategoryResponse create(CreateCategoryRequest createCategoryRequest);
}
