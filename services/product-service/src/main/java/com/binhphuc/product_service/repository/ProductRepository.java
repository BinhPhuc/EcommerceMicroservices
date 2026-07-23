package com.binhphuc.product_service.repository;

import com.binhphuc.product_service.entity.Product;

import jakarta.persistence.LockModeType;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByIdIn(List<String> productIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id IN :productIds")
    List<Product> findByIdInForUpdate(List<String> productIds);
}
