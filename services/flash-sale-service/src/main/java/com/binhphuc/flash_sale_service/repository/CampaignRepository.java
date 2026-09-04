package com.binhphuc.flash_sale_service.repository;

import com.binhphuc.flash_sale_service.entity.Campaign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, String> {
    boolean existsByIdAndIsDeletedFalse(String campaignId);
}
