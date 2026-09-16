package com.binhphuc.flash_sale_service.repository;

import com.binhphuc.flash_sale_service.entity.CampaignItem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignItemRepository extends JpaRepository<CampaignItem, String> {
    List<CampaignItem> findByCampaignIdAndIsDeletedFalse(String campaignId);
}
