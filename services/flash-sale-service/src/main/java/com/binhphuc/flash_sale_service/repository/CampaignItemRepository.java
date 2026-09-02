package com.binhphuc.flash_sale_service.repository;

import com.binhphuc.flash_sale_service.entity.CampaignItem;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignItemRepository extends JpaRepository<CampaignItem, String> {
    List<CampaignItem> findByCampaignIdAndIsDeletedFalse(String campaignId);

    boolean existsByCampaignIdAndVariantIdAndIsDeletedFalse(String campaignId, String variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM CampaignItem i WHERE i.id = :campaignItemId")
    Optional<CampaignItem> findByIdForUpdate(String campaignItemId);
}
