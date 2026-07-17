package com.binhphuc.order_service.client.product.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.binhphuc.common_web_starter.dto.ApiResponse;
import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.order_service.client.product.ProductClient;
import com.binhphuc.order_service.client.product.dto.request.GetProductByIdsRequest;
import com.binhphuc.order_service.client.product.dto.response.GetProductByIdsResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductClientImpl implements ProductClient {
    private final WebClient productClient;

    @Override
    public List<GetProductByIdsResponse> getProductsByIds(GetProductByIdsRequest request) {
        ApiResponse<List<GetProductByIdsResponse>> response = productClient
                .post()
                .uri("/products/get-by-ids")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<GetProductByIdsResponse>>>() {})
                .block();
        if (response == null) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, "Product service is not available");
        }
        if (response.getData() == null) {
            throw new BusinessException(HttpStatus.valueOf(response.getStatusCode()), response.getMessage());
        }
        return response.getData();
    }
}
