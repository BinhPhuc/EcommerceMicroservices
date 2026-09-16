package com.binhphuc.flash_sale_service.controller;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateCampaignRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.CreateCampaignResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.GetCampaignItemResponse;
import com.binhphuc.flash_sale_service.dto.order.request.CreateOrderRequest;
import com.binhphuc.flash_sale_service.publisher.impl.SoldOutPublisher;
import com.binhphuc.flash_sale_service.schedule.dto.SoldStatus;
import com.binhphuc.flash_sale_service.service.FlashSaleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/flash-sales")
public class FlashSaleController {
    private final FlashSaleService flashSaleService;
    private final SoldOutPublisher soldOutPublisher;

    @PostMapping("/campaign/create")
    public ResponseEntity<ApiResponse<CreateCampaignResponse>> createCampaign(@Valid @RequestBody CreateCampaignRequest request) {
        log.info("Creating flash sale campaign: {}", request.getName());
        CreateCampaignResponse response = flashSaleService.createCampaign(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Flash sale campaign created successfully"));
    }

    @GetMapping("/campaign/{campaignId}/products")
    public ResponseEntity<ApiResponse<List<GetCampaignItemResponse>>> getProductsByCampaignId(@PathVariable String campaignId) {
        log.info("Getting products by campaign id: {}", campaignId);
        List<GetCampaignItemResponse> response = flashSaleService.getItemsByCampaignId(campaignId);
        return ResponseEntity.ok(ApiResponse.success(response, "Products retrieved successfully"));
    }

    @PostMapping("/campaign/{campaignId}/order")
    public ResponseEntity<ApiResponse<Void>> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest,
                                                         @PathVariable String campaignId) {
        log.info("Creating order for campaign id: {}", campaignId);
        flashSaleService.createOrder(createOrderRequest, campaignId);
        return ResponseEntity.ok(ApiResponse.success(null, "Order created successfully"));
    }
}
