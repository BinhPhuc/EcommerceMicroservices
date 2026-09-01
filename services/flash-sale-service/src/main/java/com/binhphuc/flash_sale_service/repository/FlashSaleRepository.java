package com.binhphuc.flash_sale_service.repository;

import com.binhphuc.flash_sale_service.entity.FlashSale;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, String> {
    Optional<FlashSale> findByIdAndIsDeletedFalse(String flashSaleId);

    boolean existsByIdAndIsDeletedFalse(String flashSaleId);
}
