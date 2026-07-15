package com.binhphuc.product_service.service.impl;

import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;
import com.binhphuc.product_service.entity.Category;
import com.binhphuc.product_service.entity.Product;
import com.binhphuc.product_service.exception.ResourceNotFoundException;
import com.binhphuc.product_service.repository.CategoryRepository;
import com.binhphuc.product_service.repository.ProductRepository;
import com.binhphuc.product_service.service.ProductService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  @Override
  public CreateProductResponse create(CreateProductRequest productRequest) {
    Optional<Category> categoryOptional =
        categoryRepository.findById(productRequest.getCategoryId());

    if (categoryOptional.isEmpty()) {
      throw new ResourceNotFoundException("Category not found with id: " +
                                          productRequest.getCategoryId());
    }

    Product newProduct = Product.builder()
                             .name(productRequest.getName())
                             .price(productRequest.getPrice())
                             .stock(productRequest.getStock())
                             .categoryId(productRequest.getCategoryId())
                             .build();

    Product savedProduct = productRepository.save(newProduct);

    return CreateProductResponse.builder().name(savedProduct.getName()).build();
  }
}
