package com.binhphuc.flash_sale_service.controller;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateCampaignRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.CreateCampaignResponse;
import com.binhphuc.flash_sale_service.service.FlashSaleService;

import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/flash-sales")
public class FlashSaleController {
    private final FlashSaleService flashSaleService;

    @PostMapping("/campaign/create")
    public ResponseEntity<ApiResponse<CreateCampaignResponse>> createCampaign(@Valid @RequestBody CreateCampaignRequest request) {
        log.info("Creating flash sale campaign: {}", request.getName());
        CreateCampaignResponse response = flashSaleService.createCampaign(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Flash sale campaign created successfully"));
    }
}
