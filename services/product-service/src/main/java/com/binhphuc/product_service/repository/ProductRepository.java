package com.binhphuc.product_service.repository;

import com.binhphuc.product_service.entity.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByIdIn(List<String> productIds);
}
