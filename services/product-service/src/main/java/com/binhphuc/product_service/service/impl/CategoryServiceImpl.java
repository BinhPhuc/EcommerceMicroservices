package com.binhphuc.product_service.service.impl;

import com.binhphuc.common_web_starter.exception.ResourceExistException;
import com.binhphuc.common_web_starter.exception.ResourceNotFoundException;
import com.binhphuc.product_service.dto.category.CreateCategoryRequest;
import com.binhphuc.product_service.dto.category.CreateCategoryResponse;
import com.binhphuc.product_service.entity.Category;
import com.binhphuc.product_service.repository.CategoryRepository;
import com.binhphuc.product_service.service.CategoryService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;

  @Override
  public CreateCategoryResponse create(CreateCategoryRequest createCategoryRequest) {
    Optional<Category> existingCategory = categoryRepository.findByName(createCategoryRequest.getName());

    if (!existingCategory.isEmpty()) {
      throw new ResourceExistException(
          "Category with name " + createCategoryRequest.getName() +
              " already exists");
    }

    Category newCategory = Category.builder()
        .name(createCategoryRequest.getName())
        .parentID(createCategoryRequest.getParentID())
        .build();
    if (createCategoryRequest.getParentID().isEmpty()) {
      newCategory.setParentID(null);
    } else {
      Optional<Category> parentCategory = categoryRepository.findById(createCategoryRequest.getParentID());
      if (parentCategory.isEmpty()) {
        throw new ResourceNotFoundException(
            "Parent category with ID " + createCategoryRequest.getParentID() +
                " not found");
      }
    }
    Category savedCategory = categoryRepository.save(newCategory);
    return CreateCategoryResponse.builder()
        .name(savedCategory.getName())
        .parentID(savedCategory.getParentID())
        .build();
  }
}
