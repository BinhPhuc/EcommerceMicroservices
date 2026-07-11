package com.binhphuc.product_service.service.impl;

import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.entity.Category;
import com.binhphuc.product_service.entity.Product;
import com.binhphuc.product_service.repository.CategoryRepository;
import com.binhphuc.product_service.repository.ProductRepository;
import com.binhphuc.product_service.service.IProductService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  @Override
  public Product create(CreateProductRequest productRequest) {
    Optional<Category> categoryOptional =
        categoryRepository.findById(productRequest.getCategoryId());
    return null;
  }
}
