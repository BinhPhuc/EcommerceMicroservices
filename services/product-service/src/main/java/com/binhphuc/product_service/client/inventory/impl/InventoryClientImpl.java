package com.binhphuc.product_service.client.inventory.impl;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.common_web_starter.dto.ErrorResponse;
import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.product_service.client.inventory.InventoryClient;
import com.binhphuc.product_service.client.inventory.dto.request.CreateProductStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Component
public class InventoryClientImpl implements InventoryClient {
    private final WebClient inventoryClient;

    @Override
    public void createProductStock(CreateProductStockRequest request) {
        ApiResponse<Void> stockResponse = inventoryClient
                .post()
                .uri("/inventory/create-product-stock")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response
                        .bodyToMono(ErrorResponse.class)
                        .defaultIfEmpty(new ErrorResponse())
                        .map(errorResponse -> new BusinessException(HttpStatus.valueOf(errorResponse.getStatusCode()),
                                errorResponse.getMessage())))
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Void>>() {
                })
                .block();

        if (stockResponse == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "Inventory service is not available");
        }
    }
}
