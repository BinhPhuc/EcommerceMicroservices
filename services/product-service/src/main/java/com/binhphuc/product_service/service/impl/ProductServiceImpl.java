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
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductEventProducer productEventProducer;
    private final RedissonClient redissonClient;

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
        List<String> sortedProductIds = lockProductStockCommand
                .getOrderItems()
                .stream()
                .map(OrderItem::getProductId)
                .sorted()
                .toList();
        String lockKey = "lock:prduct_stock:" + String.join(",", sortedProductIds);
        log.info("Attempting to acquire lock for product stock with key: {}", lockKey);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLockAcquired = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if (!isLockAcquired) {
                throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Could not acquire lock for product stock");
            }
            Thread.sleep(4000);
            List<Product> lockedProducts = productRepository.findByIdIn(sortedProductIds);
            Map<String, Integer> productIdToQuantityMap = lockProductStockCommand
                    .getOrderItems()
                    .stream()
                    .collect(java.util.stream.Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity));
            for (Product product : lockedProducts) {
                Integer quantityToLock = productIdToQuantityMap.get(product.getId());
                if (product.getStock() < quantityToLock) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "Not enough stock for product with id: " +
                            product
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
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Thread interrupted while trying to acquire lock");
        } finally {
            log.info("Releasing lock for product stock with key: {}", lockKey);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
