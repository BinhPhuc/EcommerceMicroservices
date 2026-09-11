package com.binhphuc.inventory_service.repository;

import com.binhphuc.inventory_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    Optional<Inventory> findByVariantId(String variantId);

    List<Inventory> findByVariantIdIn(List<String> variantIds);

    @Modifying
    @Query("update Inventory i set i.reserveStock = i.reserveStock + :reserveStock where i.variantId = :variantId")
    void updateReserveStockByVariantId(String variantId, Long reserveStock);
}
