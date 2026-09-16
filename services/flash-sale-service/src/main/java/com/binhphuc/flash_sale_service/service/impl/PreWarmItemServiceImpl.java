package com.binhphuc.flash_sale_service.service.impl;

import com.binhphuc.flash_sale_service.constant.PreWarmItemConstant;
import com.binhphuc.flash_sale_service.kafka.event.dto.FlashSaleItem;
import com.binhphuc.flash_sale_service.schedule.PreWarmItemJob;
import com.binhphuc.flash_sale_service.service.PreWarmItemService;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreWarmItemServiceImpl implements PreWarmItemService {
    private final Scheduler scheduler;

    @Override
    public void preWarmItem(Instant startTime, List<FlashSaleItem> flashSaleItems,
                            String campaignId) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(PreWarmItemConstant.FLASH_SALE_ITEMS_KEY, flashSaleItems);
        jobDataMap.put(PreWarmItemConstant.CAMPAIGN_ID_KEY, campaignId);
        JobDetail jobDetail = JobBuilder.newJob()
                .ofType(PreWarmItemJob.class)
                .withIdentity("preWarmItemJob-" + campaignId, "preWarmItemGroup")
                .setJobData(jobDataMap)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("preWarmItemTrigger-" + campaignId, "preWarmItemGroup")
                .startAt(startTime)
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
