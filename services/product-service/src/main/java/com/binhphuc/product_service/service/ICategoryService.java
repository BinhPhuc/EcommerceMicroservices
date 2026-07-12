package com.binhphuc.product_service.service;

import com.binhphuc.product_service.dto.category.CreateCategoryRequest;
import com.binhphuc.product_service.dto.category.CreateCategoryResponse;

public interface ICategoryService {
  CreateCategoryResponse create(CreateCategoryRequest createCategoryRequest);
}
