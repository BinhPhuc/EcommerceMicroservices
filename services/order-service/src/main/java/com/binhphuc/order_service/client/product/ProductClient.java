package com.binhphuc.order_service.client.product;

import java.util.List;

import com.binhphuc.order_service.client.product.dto.request.GetProductByIdsRequest;
import com.binhphuc.order_service.client.product.dto.request.UpdateProductStockRequest;
import com.binhphuc.order_service.client.product.dto.response.GetProductByIdsResponse;

public interface ProductClient {
    List<GetProductByIdsResponse> getProductsByIds(GetProductByIdsRequest resquest);

    void updateProductStock(UpdateProductStockRequest updateProductStockRequest);
}
