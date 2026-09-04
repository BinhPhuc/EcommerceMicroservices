package com.binhphuc.flash_sale_service.client.product.impl;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.common_web_starter.dto.ErrorResponse;
import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.flash_sale_service.client.product.ProductClient;
import com.binhphuc.flash_sale_service.client.product.dto.request.GetFlashSaleItemRequest;
import com.binhphuc.flash_sale_service.client.product.dto.response.GetProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {
    @Qualifier("productClient")
    private final WebClient productClient;

    @Override
    public List<GetProductResponse> getFlashSaleItems(GetFlashSaleItemRequest getFlashSaleItemRequest) {
        ApiResponse<List<GetProductResponse>> rawResponse = productClient
                .post()
                .uri("/products/get-flash-sale-items")
                .bodyValue(getFlashSaleItemRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response
                        .bodyToMono(ErrorResponse.class)
                        .defaultIfEmpty(new ErrorResponse())
                        .map(errorResponse -> new BusinessException(HttpStatus.valueOf(errorResponse.getStatusCode()),
                                errorResponse.getMessage())))
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<GetProductResponse>>>() {
                })
                .block();

        if (rawResponse == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "Inventory service is not available");
        }
        return rawResponse.getData();
    }
}
