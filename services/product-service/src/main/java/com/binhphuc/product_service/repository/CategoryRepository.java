package com.binhphuc.product_service.repository;

import com.binhphuc.product_service.entity.Category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByName(String name);

    boolean existsByIdAndIsDeletedFalse(String id);

    Optional<Category> findByIdAndIsDeletedFalse(String id);

    Optional<Category> findByNameAndIsDeletedFalse(String name);
}
