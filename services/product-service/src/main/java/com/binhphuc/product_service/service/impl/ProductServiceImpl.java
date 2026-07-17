package com.binhphuc.product_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.request.GetProductByIdsRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;
import com.binhphuc.product_service.dto.product.response.GetProductByIdsResponse;
import com.binhphuc.product_service.entity.Category;
import com.binhphuc.product_service.entity.Product;
import com.binhphuc.product_service.repository.CategoryRepository;
import com.binhphuc.product_service.repository.ProductRepository;
import com.binhphuc.product_service.service.ProductService;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public CreateProductResponse create(CreateProductRequest productRequest) {
        Optional<Category> categoryOptional = categoryRepository.findById(productRequest.getCategoryId());

        if (categoryOptional.isEmpty()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Category not found with id: " + productRequest
                    .getCategoryId());
        }

        Product newProduct = Product
                .builder()
                .name(productRequest.getName())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .categoryId(productRequest.getCategoryId())
                .build();

        Product savedProduct = productRepository.save(newProduct);

        return CreateProductResponse.builder().name(savedProduct.getName()).build();
    }

    @Override
    public List<GetProductByIdsResponse> getProductByIds(GetProductByIdsRequest getProductByIdsRequest) {
        List<Product> products = productRepository.findByIdIn(getProductByIdsRequest.getProductIds());
        List<GetProductByIdsResponse> responseList = products
                .stream()
                .map(product -> GetProductByIdsResponse
                        .builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .categoryId(product.getCategoryId())
                        .isDeleted(product.getIsDeleted())
                        .build())
                .toList();
        return responseList;
    }
}
