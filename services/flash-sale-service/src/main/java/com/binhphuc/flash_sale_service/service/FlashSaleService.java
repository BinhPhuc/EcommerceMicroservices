package com.binhphuc.flash_sale_service.service;

import java.util.List;

import com.binhphuc.flash_sale_service.dto.flash_sale.request.CreateCampaignRequest;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.CreateCampaignResponse;
import com.binhphuc.flash_sale_service.dto.flash_sale.response.GetCampaignItemResponse;
import org.quartz.SchedulerException;

public interface FlashSaleService {
    CreateCampaignResponse createCampaign(CreateCampaignRequest createCampaignRequest) throws SchedulerException;

    List<GetCampaignItemResponse> getItemsByCampaignId(String campaignId);
}
