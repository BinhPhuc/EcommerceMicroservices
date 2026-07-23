package com.binhphuc.product_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.request.GetProductByIdsRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;
import com.binhphuc.product_service.dto.product.response.GetProductByIdsResponse;
import com.binhphuc.product_service.entity.Product;
import com.binhphuc.product_service.kafka.event.ProductLockedEvent;
import com.binhphuc.product_service.kafka.event.dto.order.OrderItem;
import com.binhphuc.product_service.kafka.event.dto.product.LockProductStockCommand;
import com.binhphuc.product_service.kafka.producer.ProductEventProducer;
import com.binhphuc.product_service.repository.CategoryRepository;
import com.binhphuc.product_service.repository.ProductRepository;
import com.binhphuc.product_service.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductEventProducer productEventProducer;

    @Override
    public CreateProductResponse create(CreateProductRequest productRequest) {
        if (!categoryRepository.existsByIdAndIsDeletedFalse(productRequest.getCategoryId())) {
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

    @Override
    @Transactional
    public void lockProductStock(LockProductStockCommand lockProductStockCommand) {
        List<Product> products = new ArrayList<>();
        List<String> productIds = lockProductStockCommand
                .getOrderItems()
                .stream()
                .map(OrderItem::getProductId)
                .toList();
        List<Product> lockedProducts = productRepository.findByIdInForUpdate(productIds);
        Map<String, Integer> productIdToQuantityMap = lockProductStockCommand
                .getOrderItems()
                .stream()
                .collect(java.util.stream.Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity));
        for (Product product : lockedProducts) {
            Integer quantityToLock = productIdToQuantityMap.get(product.getId());
            if (product.getStock() < quantityToLock) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Not enough stock for product with id: " + product
                        .getId());
            }
            product.setStock(product.getStock() - quantityToLock);
            products.add(product);
        }
        productRepository.saveAll(products);
        productEventProducer
                .sendLockProductStockEvent(ProductLockedEvent
                        .builder()
                        .orderId(lockProductStockCommand.getOrderId())
                        .build());
    }
}
