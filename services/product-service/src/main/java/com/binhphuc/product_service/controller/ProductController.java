package com.binhphuc.product_service.controller;

import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  @PostMapping("/create")
  public ResponseEntity<CreateProductResponse>
  createProduct(@RequestBody CreateProductRequest productRequest) {
    log.info("Creating product: {}", productRequest.getName());
    // Product createdProduct = productService.createProduct(product);
    return ResponseEntity.ok("hihi");
  }
}
