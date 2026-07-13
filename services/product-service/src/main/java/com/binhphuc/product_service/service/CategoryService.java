package com.binhphuc.product_service.service;

import com.binhphuc.product_service.dto.category.CreateCategoryRequest;
import com.binhphuc.product_service.dto.category.CreateCategoryResponse;

public interface CategoryService {
  CreateCategoryResponse create(CreateCategoryRequest createCategoryRequest);
}
