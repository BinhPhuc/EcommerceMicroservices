package com.binhphuc.product_service.service;

import com.binhphuc.product_service.dto.category.request.CreateCategoryRequest;
import com.binhphuc.product_service.dto.category.response.CreateCategoryResponse;
import com.binhphuc.product_service.entity.Category;

public interface CategoryService {
    CreateCategoryResponse create(CreateCategoryRequest createCategoryRequest);

    Category getById(String categoryId);

    boolean existsById(String categoryId);
}
