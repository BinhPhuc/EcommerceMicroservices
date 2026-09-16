package com.binhphuc.product_service.controller;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.product_service.dto.falshsale.FlashSaleItem;
import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.request.GetFlashSaleItemRequest;
import com.binhphuc.product_service.dto.product.request.GetProductByIdsRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;
import com.binhphuc.product_service.dto.product.response.GetProductResponse;
import com.binhphuc.product_service.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
            @Valid @RequestBody CreateProductRequest productRequest) {
        log.info("Creating product: {}", productRequest.getName());
        CreateProductResponse response = productService.create(productRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Product created successfully"));
    }

    @PostMapping("/get-by-ids")
    public ResponseEntity<ApiResponse<List<GetProductResponse>>> getProductsByIds(
            @Valid @RequestBody GetProductByIdsRequest getProductByIdsRequest) {
        log.info("Getting products by ids");
        List<GetProductResponse> response = productService.getProductByIds(getProductByIdsRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Products retrieved successfully"));
    }

    @PostMapping("/get-flash-sale-items")
    public ResponseEntity<ApiResponse<List<GetProductResponse>>> getFlashSaleItems(
            @Valid @RequestBody GetFlashSaleItemRequest getFlashSaleItemRequest) {
        log.info("Getting flash sale items");
        List<GetProductResponse> response = productService.getFlashSaleItems(getFlashSaleItemRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Flash sale items retrieved successfully"));
    }
}
