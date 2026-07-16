package com.binhphuc.order_service.client.product;

import java.util.List;

import com.binhphuc.order_service.client.product.dto.ProductDTO;

public interface ProductClient {
  List<ProductDTO> getProductsByIds(List<String> productIDs);
}
