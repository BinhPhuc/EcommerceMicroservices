package com.binhphuc.flash_sale_service.controller;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateFlashSaleRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.ReserveFlashSaleItemRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.CreateFlashSaleResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.GetFlashSaleItemResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.ReserveFlashSaleItemResponse;
import com.binhphuc.flash_sale_service.service.FlashSaleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/flash-sales")
public class FlashSaleController {
    private final FlashSaleService flashSaleService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateFlashSaleResponse>> createFlashSale(
            @Valid @RequestBody CreateFlashSaleRequest createFlashSaleRequest) {
        log.info("Creating flash sale: {}", createFlashSaleRequest.getName());
        CreateFlashSaleResponse response = flashSaleService.create(createFlashSaleRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Flash sale created successfully"));
    }

    @GetMapping("/{flashSaleId}/items")
    public ResponseEntity<ApiResponse<List<GetFlashSaleItemResponse>>> getFlashSaleItems(
            @PathVariable String flashSaleId) {
        log.info("Getting flash sale items by flash sale id: {}", flashSaleId);
        List<GetFlashSaleItemResponse> response = flashSaleService.getItemsByFlashSaleId(flashSaleId);
        return ResponseEntity.ok(ApiResponse.success(response, "Flash sale items retrieved successfully"));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<ReserveFlashSaleItemResponse>> reserveFlashSaleItem(
            @Valid @RequestBody ReserveFlashSaleItemRequest reserveFlashSaleItemRequest) {
        log.info("Reserving flash sale item: {}", reserveFlashSaleItemRequest.getFlashSaleItemId());
        ReserveFlashSaleItemResponse response = flashSaleService.reserveItem(reserveFlashSaleItemRequest);
        return ResponseEntity.ok(ApiResponse.success(response, "Flash sale item reserved successfully"));
    }
}
