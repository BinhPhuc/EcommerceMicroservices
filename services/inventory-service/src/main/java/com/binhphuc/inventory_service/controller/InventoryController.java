package com.binhphuc.inventory_service.controller;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.inventory_service.dto.request.CreateProductStockRequest;
import com.binhphuc.inventory_service.dto.request.GetStockByVariantIdsRequest;
import com.binhphuc.inventory_service.dto.response.GetStockByVariantIdsResponse;
import com.binhphuc.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/create-product-stock")
    public ResponseEntity<ApiResponse<Void>> createProductStock(@Valid @RequestBody CreateProductStockRequest request) {
        inventoryService.createProductStock(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Product stock created successfully"));
    }

    @PostMapping("/stock")
    public ResponseEntity<ApiResponse<List<GetStockByVariantIdsResponse>>> getStockByVariantId(@RequestBody GetStockByVariantIdsRequest request) {
        List<GetStockByVariantIdsResponse> responseList = inventoryService.getStockByVariantId(request);
        return ResponseEntity.ok(ApiResponse.success(responseList, "Stock retrieved successfully"));
    }
}
