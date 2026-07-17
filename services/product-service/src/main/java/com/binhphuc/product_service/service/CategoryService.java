package com.binhphuc.product_service.service;

import com.binhphuc.product_service.dto.category.request.CreateCategoryRequest;
import com.binhphuc.product_service.dto.category.response.CreateCategoryResponse;

public interface CategoryService {
    CreateCategoryResponse create(CreateCategoryRequest createCategoryRequest);
}
