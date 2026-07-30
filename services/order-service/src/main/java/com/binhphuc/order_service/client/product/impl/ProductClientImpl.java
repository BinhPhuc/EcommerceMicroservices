package com.binhphuc.order_service.client.product.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.common_web_starter.dto.ErrorResponse;
import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.order_service.client.product.ProductClient;
import com.binhphuc.order_service.client.product.dto.request.GetProductByIdsRequest;
import com.binhphuc.order_service.client.product.dto.request.UpdateProductStockRequest;
import com.binhphuc.order_service.client.product.dto.response.GetProductByIdsResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {
    private final WebClient productClient;

    @Override
    public List<GetProductByIdsResponse> getProductsByIds(GetProductByIdsRequest request) {
        ApiResponse<List<GetProductByIdsResponse>> productsResponse = productClient
                .post()
                .uri("/products/get-by-ids")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response
                        .bodyToMono(ErrorResponse.class)
                        .defaultIfEmpty(new ErrorResponse())
                        .map(errorResponse -> new BusinessException(HttpStatus.valueOf(errorResponse.getStatusCode()),
                                errorResponse
                                        .getMessage())))
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<GetProductByIdsResponse>>>() {})
                .block();

        if (productsResponse == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "Product service is not available");
        }

        if (productsResponse.getData() == null) {
            throw new BusinessException(HttpStatus.valueOf(productsResponse.getStatusCode()), productsResponse
                    .getMessage());
        }

        return productsResponse.getData();
    }

    @Override
    public void updateProductStock(UpdateProductStockRequest updateProductStockRequest) {
        ApiResponse<Void> updateStockResponse = productClient
                .patch()
                .uri("/products/update-stock")
                .bodyValue(updateProductStockRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response
                        .bodyToMono(ErrorResponse.class)
                        .defaultIfEmpty(new ErrorResponse())
                        .map(errorResponse -> new BusinessException(HttpStatus.valueOf(errorResponse.getStatusCode()),
                                errorResponse
                                        .getMessage())))
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Void>>() {})
                .block();

        if (updateStockResponse == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "Product service is not available");
        }

        HttpStatus responseStatus = HttpStatus.valueOf(updateStockResponse.getStatusCode());

        if (!responseStatus.is2xxSuccessful()) {
            throw new BusinessException(responseStatus, updateStockResponse.getMessage());
        }
    }
}
