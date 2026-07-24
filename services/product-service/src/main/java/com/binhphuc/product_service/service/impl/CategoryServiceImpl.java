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
    public Category getById(String categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findByIdAndIsDeletedFalse(categoryId);
        if (!categoryOptional.isPresent()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Category not found with id: " + categoryId);
        }
        return categoryOptional.get();
    }

    @Override
    public boolean existsById(String categoryId) {
        return categoryRepository.existsByIdAndIsDeletedFalse(categoryId);
    }

    @Override
    public CreateCategoryResponse create(CreateCategoryRequest createCategoryRequest) {
        Optional<Category> existingCategory = categoryRepository
                .findByIdAndIsDeletedFalse(createCategoryRequest.getName());

        if (!existingCategory.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Category with name " + createCategoryRequest
                    .getName() + " already exists");
        }

        Category newCategory = Category
                .builder()
                .name(createCategoryRequest.getName())
                .parentId(createCategoryRequest.getParentId())
                .build();
        String parentId = createCategoryRequest.getParentId();

        if (parentId == null || parentId.isBlank()) {
            newCategory.setParentId(null);
        } else if (!existsById(parentId)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Parent category with id " + parentId +
                    " does not exist");
        }

        Category savedCategory = categoryRepository.save(newCategory);
        return CreateCategoryResponse
                .builder()
                .name(savedCategory.getName())
                .parentId(savedCategory.getParentId())
                .build();
    }
}
