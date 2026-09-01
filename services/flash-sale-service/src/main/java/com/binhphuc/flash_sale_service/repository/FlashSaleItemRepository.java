package com.binhphuc.flash_sale_service.repository;

import com.binhphuc.flash_sale_service.entity.FlashSaleItem;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, String> {
    List<FlashSaleItem> findByFlashSaleIdAndIsDeletedFalse(String flashSaleId);

    boolean existsByFlashSaleIdAndVariantIdAndIsDeletedFalse(String flashSaleId, String variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM FlashSaleItem i WHERE i.id = :flashSaleItemId")
    Optional<FlashSaleItem> findByIdForUpdate(String flashSaleItemId);
}
