package com.binhphuc.product_service.controller;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;
import com.binhphuc.product_service.service.ProductService;
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
@RequestMapping("/api/v1/products")
public class ProductController {
  private final ProductService productService;

  @PostMapping("/create")
  public ResponseEntity<ApiResponse<CreateProductResponse>> createProduct(
      @RequestBody CreateProductRequest productRequest) {
    log.info("Creating product: {}", productRequest.getName());
    CreateProductResponse response = productService.create(productRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response,
        "Product created successfully"));
  }
}
