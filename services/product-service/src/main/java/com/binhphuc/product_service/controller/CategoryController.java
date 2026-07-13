package com.binhphuc.product_service.controller;

import com.binhphuc.product_service.dto.common.ApiResponse;
import com.binhphuc.product_service.dto.category.CreateCategoryRequest;
import com.binhphuc.product_service.dto.category.CreateCategoryResponse;
import com.binhphuc.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
  private final CategoryService categoryService;

  @PostMapping("/create")
  public ResponseEntity<ApiResponse<CreateCategoryResponse>> createCategory(
      @RequestBody CreateCategoryRequest createCategoryRequest) {
    log.info("Create category: {}", createCategoryRequest.getName());
    CreateCategoryResponse response = categoryService.create(createCategoryRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.created(response, "Category created successfully"));
  }
}
