package com.binhphuc.product_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.product_service.dto.category.request.CreateCategoryRequest;
import com.binhphuc.product_service.dto.category.response.CreateCategoryResponse;
import com.binhphuc.product_service.entity.Category;
import com.binhphuc.product_service.repository.CategoryRepository;
import com.binhphuc.product_service.service.CategoryService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CreateCategoryResponse create(CreateCategoryRequest createCategoryRequest) {
        Optional<Category> existingCategory = categoryRepository.findByName(createCategoryRequest.getName());

        if (!existingCategory.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Category with name " + createCategoryRequest
                    .getName() + " already exists");
        }

        Category newCategory = Category
                .builder()
                .name(createCategoryRequest.getName())
                .parentId(createCategoryRequest.getParentId())
                .build();
        if (createCategoryRequest.getParentId().isEmpty()) {
            newCategory.setParentId(null);
        } else {
            Optional<Category> parentCategory = categoryRepository.findById(createCategoryRequest.getParentId());
            if (parentCategory.isEmpty()) {
                throw new BusinessException(HttpStatus.NOT_FOUND, "Parent category not found with id: " +
                        createCategoryRequest
                                .getParentId());
            }
        }
        Category savedCategory = categoryRepository.save(newCategory);
        return CreateCategoryResponse
                .builder()
                .name(savedCategory.getName())
                .parentId(savedCategory.getParentId())
                .build();
    }
}
