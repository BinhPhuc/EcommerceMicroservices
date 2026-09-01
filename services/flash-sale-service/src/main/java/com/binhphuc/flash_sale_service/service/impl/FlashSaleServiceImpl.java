package com.binhphuc.flash_sale_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.flash_sale_service.context.holder.UserContextHolder;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateFlashSaleItemRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateFlashSaleRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.request.ReserveFlashSaleItemRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.CreateFlashSaleResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.GetFlashSaleItemResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.ReserveFlashSaleItemResponse;
import com.binhphuc.flash_sale_service.entity.FlashSale;
import com.binhphuc.flash_sale_service.entity.FlashSaleItem;
import com.binhphuc.flash_sale_service.enums.FlashSaleStatus;
import com.binhphuc.flash_sale_service.kafka.event.FlashSaleItemReservedEvent;
import com.binhphuc.flash_sale_service.kafka.producer.FlashSaleEventProducer;
import com.binhphuc.flash_sale_service.repository.FlashSaleItemRepository;
import com.binhphuc.flash_sale_service.repository.FlashSaleRepository;
import com.binhphuc.flash_sale_service.service.FlashSaleService;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashSaleServiceImpl implements FlashSaleService {
    private final FlashSaleRepository flashSaleRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;
    private final FlashSaleEventProducer flashSaleEventProducer;
    private final RedissonClient redissonClient;

    @Override
    @Transactional
    public CreateFlashSaleResponse create(CreateFlashSaleRequest createFlashSaleRequest) {
        Instant startedAt = createFlashSaleRequest.getStartedAt();
        Instant endedAt = createFlashSaleRequest.getEndedAt();
        if (!startedAt.isBefore(endedAt)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Flash sale start time must be before end time");
        }
        Set<String> variantIds = new HashSet<>();
        for (CreateFlashSaleItemRequest itemRequest : createFlashSaleRequest.getItems()) {
            if (!variantIds.add(itemRequest.getVariantId())) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Duplicated flash sale item with variant id: " +
                        itemRequest.getVariantId());
            }
        }
        FlashSale newFlashSale = FlashSale
                .builder()
                .name(createFlashSaleRequest.getName())
                .description(createFlashSaleRequest.getDescription())
                .status(FlashSaleStatus.SCHEDULED)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .build();
        FlashSale savedFlashSale = flashSaleRepository.save(newFlashSale);
        List<FlashSaleItem> flashSaleItemList = createFlashSaleRequest.getItems().stream().map(itemRequest -> {
            FlashSaleItem newFlashSaleItem = FlashSaleItem
                    .builder()
                    .flashSaleId(savedFlashSale.getId())
                    .productId(itemRequest.getProductId())
                    .variantId(itemRequest.getVariantId())
                    .flashPrice(itemRequest.getFlashPrice())
                    .stock(itemRequest.getStock())
                    .soldQuantity(0L)
                    .purchaseLimit(itemRequest.getPurchaseLimit())
                    .build();
            return newFlashSaleItem;
        }).toList();
        flashSaleItemRepository.saveAll(flashSaleItemList);
        return CreateFlashSaleResponse
                .builder()
                .id(savedFlashSale.getId())
                .name(savedFlashSale.getName())
                .status(savedFlashSale.getStatus())
                .startedAt(savedFlashSale.getStartedAt())
                .endedAt(savedFlashSale.getEndedAt())
                .build();
    }

    @Override
    @Cacheable(value = "flash-sale-items", key = "#flashSaleId", condition = "#flashSaleId != null")
    public List<GetFlashSaleItemResponse> getItemsByFlashSaleId(String flashSaleId) {
        if (!flashSaleRepository.existsByIdAndIsDeletedFalse(flashSaleId)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Flash sale not found with id: " + flashSaleId);
        }
        return flashSaleItemRepository
                .findByFlashSaleIdAndIsDeletedFalse(flashSaleId)
                .stream()
                .map(flashSaleItem -> GetFlashSaleItemResponse
                        .builder()
                        .id(flashSaleItem.getId())
                        .productId(flashSaleItem.getProductId())
                        .variantId(flashSaleItem.getVariantId())
                        .flashPrice(flashSaleItem.getFlashPrice())
                        .stock(flashSaleItem.getStock())
                        .soldQuantity(flashSaleItem.getSoldQuantity())
                        .purchaseLimit(flashSaleItem.getPurchaseLimit())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = "flash-sale-items", allEntries = true)
    public ReserveFlashSaleItemResponse reserveItem(ReserveFlashSaleItemRequest reserveFlashSaleItemRequest) {
        String flashSaleItemId = reserveFlashSaleItemRequest.getFlashSaleItemId();
        int quantity = reserveFlashSaleItemRequest.getQuantity();
        String userId = UserContextHolder.getUserContext().getUserId();
        String lockKey = "lock:flash_sale_item:" + flashSaleItemId;
        log.info("Attempting to acquire lock for flash sale item with key: {}", lockKey);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            boolean isLockAcquired = lock.tryLock(10, 10, TimeUnit.SECONDS);
            if (!isLockAcquired) {
                throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Could not acquire lock for flash sale item");
            }
            FlashSaleItem flashSaleItem = flashSaleItemRepository
                    .findByIdForUpdate(flashSaleItemId)
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                            "Flash sale item not found with id: " + flashSaleItemId));
            FlashSale flashSale = flashSaleRepository
                    .findByIdAndIsDeletedFalse(flashSaleItem.getFlashSaleId())
                    .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND,
                            "Flash sale not found with id: " + flashSaleItem.getFlashSaleId()));
            requireRunning(flashSale);
            long remainingStock = flashSaleItem.getStock() - flashSaleItem.getSoldQuantity();
            if (remainingStock < quantity) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "Not enough stock for flash sale item with id: " +
                        flashSaleItemId);
            }
            requirePurchaseLimit(flashSale, flashSaleItem, userId, quantity);
            flashSaleItem.setSoldQuantity(flashSaleItem.getSoldQuantity() + quantity);
            FlashSaleItem savedFlashSaleItem = flashSaleItemRepository.save(flashSaleItem);
            flashSaleEventProducer
                    .sendFlashSaleItemReservedEvent(FlashSaleItemReservedEvent
                            .builder()
                            .flashSaleId(flashSale.getId())
                            .flashSaleItemId(savedFlashSaleItem.getId())
                            .productId(savedFlashSaleItem.getProductId())
                            .variantId(savedFlashSaleItem.getVariantId())
                            .userId(userId)
                            .flashPrice(savedFlashSaleItem.getFlashPrice())
                            .quantity(quantity)
                            .build());
            return ReserveFlashSaleItemResponse
                    .builder()
                    .flashSaleItemId(savedFlashSaleItem.getId())
                    .reservedQuantity(quantity)
                    .remainingStock(savedFlashSaleItem.getStock() - savedFlashSaleItem.getSoldQuantity())
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Thread interrupted while trying to acquire lock");
        } finally {
            log.info("Releasing lock for flash sale item with key: {}", lockKey);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void requireRunning(FlashSale flashSale) {
        if (flashSale.getStatus() == FlashSaleStatus.CANCELLED) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Flash sale is cancelled with id: " + flashSale
                    .getId());
        }
        Instant now = Instant.now();
        if (now.isBefore(flashSale.getStartedAt()) || !now.isBefore(flashSale.getEndedAt())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Flash sale is not running with id: " + flashSale
                    .getId());
        }
    }

    private void requirePurchaseLimit(FlashSale flashSale, FlashSaleItem flashSaleItem, String userId, int quantity) {
        String purchasedKey = "flash_sale_item:purchased:" + flashSaleItem.getId() + ":" + userId;
        RAtomicLong purchasedCounter = redissonClient.getAtomicLong(purchasedKey);
        long purchasedQuantity = purchasedCounter.addAndGet(quantity);
        purchasedCounter.expire(Duration.between(Instant.now(), flashSale.getEndedAt()));
        if (purchasedQuantity > flashSaleItem.getPurchaseLimit()) {
            purchasedCounter.addAndGet(-quantity);
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Purchase limit exceeded for flash sale item with id: " +
                    flashSaleItem.getId());
        }
    }
}
