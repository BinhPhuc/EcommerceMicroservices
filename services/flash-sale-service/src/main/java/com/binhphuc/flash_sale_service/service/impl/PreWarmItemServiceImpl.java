package com.binhphuc.flash_sale_service.service.impl;

import com.binhphuc.flash_sale_service.constant.PreWarmItemConstant;
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
    public void preWarmItem(Instant startTime, List<String> productIds, List<String> variantIds) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(PreWarmItemConstant.PRODUCT_IDS_KEY, productIds);
        jobDataMap.put(PreWarmItemConstant.VARIANT_IDS_KEY, variantIds);
        JobDetail jobDetail = JobBuilder.newJob()
                .ofType(PreWarmItemJob.class)
                .withIdentity("preWarmItemJob", "preWarmItemGroup")
                .setJobData(jobDataMap)
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("preWarmItemTrigger", "preWarmItemGroup")
                .startAt(startTime)
                .build();
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
