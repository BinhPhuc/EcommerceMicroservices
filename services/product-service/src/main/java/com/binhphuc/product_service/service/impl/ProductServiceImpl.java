package com.binhphuc.product_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.product_service.client.inventory.InventoryClient;
import com.binhphuc.product_service.client.inventory.dto.request.CreateProductStockRequest;
import com.binhphuc.common_core.context.holder.UserContextHolder;
import com.binhphuc.product_service.client.inventory.dto.request.GetStockByVariantIdsRequest;
import com.binhphuc.product_service.client.inventory.dto.response.GetStockByVariantIdsResponse;
import com.binhphuc.product_service.dto.product.request.CreateProductRequest;
import com.binhphuc.product_service.dto.product.request.GetFlashSaleItemRequest;
import com.binhphuc.product_service.dto.product.request.GetProductByIdsRequest;
import com.binhphuc.product_service.dto.product.response.CreateProductResponse;
import com.binhphuc.product_service.dto.product.response.GetProductResponse;
import com.binhphuc.product_service.entity.Product;
import com.binhphuc.product_service.entity.ProductImage;
import com.binhphuc.product_service.entity.ProductVariant;
import com.binhphuc.product_service.kafka.event.dto.order.OrderItem;
import com.binhphuc.product_service.kafka.command.LockProductStockCommand;
import com.binhphuc.product_service.kafka.producer.ProductEventProducer;
import com.binhphuc.product_service.repository.CategoryRepository;
import com.binhphuc.product_service.repository.ProductImageRepository;
import com.binhphuc.product_service.repository.ProductRepository;
import com.binhphuc.product_service.repository.ProductVariantRepository;
import com.binhphuc.product_service.service.ProductService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductEventProducer productEventProducer;
    private final InventoryClient inventoryClient;
    private final RedissonClient redissonClient;
    @Qualifier("redisCacheManager")
    private final RedisCacheManager redisCacheManager;

    @Override
    @Transactional
    public CreateProductResponse create(CreateProductRequest productRequest) {
        if (!categoryRepository.existsByIdAndIsDeletedFalse(productRequest.getCategoryId())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Category not found with id: " + productRequest
                    .getCategoryId());
        }
        if (productVariantRepository.existsBySkuAndIsDeletedFalse(productRequest.getVariant().getSku())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Product variant with SKU already exists: " +
                    productRequest.getVariant().getSku());
        }
        long totalThumbnailCount = productRequest.getImages().stream().filter(productImage -> productImage.getIsThumbnail()).count();
        if (totalThumbnailCount != 1) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "There must be exactly one thumbnail image");
        }
        productRequest.getImages().forEach(productImage -> {
            if (productImage.getIsThumbnail() && productImage.getDisplayOrder() != 1) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Thumbnail image must have display order 1");
            }
        });
        String sellerId = UserContextHolder.getUserContext().getUserId();
        Product newProduct = Product
                .builder()
                .name(productRequest.getName())
                .sellerId(sellerId)
                .status(productRequest.getStatus())
                .unitsSold(0L)
                .description(productRequest.getDescription())
                .categoryId(productRequest.getCategoryId())
                .build();
        Product savedProduct = productRepository.save(newProduct);
        List<ProductImage> productImageList = productRequest.getImages().stream().map(productImage -> {
            ProductImage newProductImage = ProductImage
                    .builder()
                    .productId(savedProduct.getId())
                    .url(productImage.getUrl())
                    .isThumbnail(productImage.getIsThumbnail())
                    .displayOrder(productImage.getDisplayOrder())
                    .build();
            return newProductImage;
        }).toList();
        productImageRepository.saveAll(productImageList);
        ProductVariant newProductVariant = ProductVariant
                .builder()
                .sku(productRequest.getVariant().getSku())
                .attributes(productRequest.getVariant().getAttributes())
                .productId(savedProduct.getId())
                .price(productRequest.getVariant().getPrice())
                .build();
        ProductVariant savedProductVariant = productVariantRepository.save(newProductVariant);
        inventoryClient.createProductStock(CreateProductStockRequest.builder()
                .stock(productRequest.getStock())
                .variantId(savedProductVariant.getId())
                .build());
        return CreateProductResponse.builder().name(savedProduct.getName()).build();
    }

    private Product cacheProductById(String productId) {
        Cache productCache = redisCacheManager.getCache("products");
        Product cachedProduct = productCache.get(productId, Product.class);
        if (cachedProduct != null) {
            return cachedProduct;
        }
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Product not found with id: " +
                        productId));
        productCache.put(productId, product);
        return product;
    }

    @Override
    public List<GetProductResponse> getProductByIds(GetProductByIdsRequest getProductByIdsRequest) {
        List<Product> products = new ArrayList<>();
        for (String productId : getProductByIdsRequest.getProductIds()) {
            Product product = cacheProductById(productId);
            products.add(product);
        }
        // List<GetProductByIdsResponse> responseList = products
        //         .stream()
        //         .map(product -> GetProductByIdsResponse
        //                 .builder()
        //                 .id(product.getId())
        //                 .name(product.getName())
        //                 .price(product.getPrice())
        //                 .stock(product.getStock())
        //                 .categoryId(product.getCategoryId())
        //                 .isDeleted(product.getIsDeleted())
        //                 .build())
        //         .toList();
        return List.of();
    }

    @Override
    @Caching(cacheable = {
            @Cacheable(cacheManager = "caffeineCacheManager", value = "products", key = "#getFlashSaleItemRequest.getCacheKey()"),
            @Cacheable(cacheManager = "redisCacheManager", value = "products", key = "#getFlashSaleItemRequest.getCacheKey()")
    })
    public List<GetProductResponse> getFlashSaleItems(GetFlashSaleItemRequest getFlashSaleItemRequest) {
        List<String> productIds = new ArrayList<>();
        List<String> variantIds = new ArrayList<>();
        getFlashSaleItemRequest.getFlashSaleItems().forEach(flashSaleItem -> {
            productIds.add(flashSaleItem.getProductId());
            variantIds.add(flashSaleItem.getVariantId());
        });
        List<GetStockByVariantIdsResponse> getStockByVariantIdsResponses = inventoryClient.getStock(GetStockByVariantIdsRequest.builder().variantIds(variantIds).build());
        List<Product> products = productRepository.findByIdIn(productIds);
        List<ProductVariant> productVariants = productVariantRepository.findByIdIn(variantIds);
        // TODO: 1 product has only 1 variant, must change this later
        Map<String, ProductVariant> productIdToProductVariant = new HashMap<>();
        Map<String, Long> variantIdToStock = new HashMap<>();
        productVariants.stream().forEach(productVariant ->
                productIdToProductVariant.put(productVariant.getProductId(), productVariant));
        getStockByVariantIdsResponses.forEach(response ->
                variantIdToStock.put(response.getVariantId(), response.getStock()));
        List<GetProductResponse> responses = products.stream().map(product ->
                GetProductResponse
                        .builder()
                        .name(product.getName())
                        .description(product.getDescription())
                        .unitsSold(0L)
                        .sku(productIdToProductVariant.get(product.getId()).getSku())
                        .variants(productIdToProductVariant.get(product.getId()).getAttributes())
                        .stock(variantIdToStock.get(productIdToProductVariant.get(product.getId()).getId()))
                        .build()
        ).toList();
        return responses;
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
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
        // try {
        //     boolean isLockAcquired = lock.tryLock(10, 10, TimeUnit.SECONDS);
        //     if (!isLockAcquired) {
        //         throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
        //                 "Could not acquire lock for product stock");
        //     }
        //     List<Product> lockedProducts = productRepository.findByIdIn(sortedProductIds);
        //     Map<String, Integer> productIdToQuantityMap = lockProductStockCommand
        //             .getOrderItems()
        //             .stream()
        //             .collect(java.util.stream.Collectors.toMap(OrderItem::getProductId, OrderItem::getQuantity));
        //     for (Product product : lockedProducts) {
        //         Integer quantityToLock = productIdToQuantityMap.get(product.getId());
        //         if (product.getStock() < quantityToLock) {
        //             throw new BusinessException(HttpStatus.BAD_REQUEST, "Not enough stock for product with id: " +
        //                     product
        //                             .getId());
        //         }
        //         product.setStock(product.getStock() - quantityToLock);
        //         products.add(product);
        //     }
        //     productRepository.saveAll(products);
        //     productEventProducer
        //             .sendLockProductStockEvent(ProductLockedEvent
        //                     .builder()
        //                     .orderId(lockProductStockCommand.getOrderId())
        //                     .build());
        // } catch (InterruptedException e) {
        //     Thread.currentThread().interrupt();
        //     throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
        //             "Thread interrupted while trying to acquire lock");
        // } finally {
        //     log.info("Releasing lock for product stock with key: {}", lockKey);
        //     if (lock.isHeldByCurrentThread()) {
        //         lock.unlock();
        //     }
        // }
    }
}
