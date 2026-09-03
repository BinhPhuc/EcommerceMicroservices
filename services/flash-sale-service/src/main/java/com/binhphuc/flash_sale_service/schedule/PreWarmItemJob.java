package com.binhphuc.flash_sale_service.schedule;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.flash_sale_service.constant.PreWarmItemConstant;
import com.binhphuc.flash_sale_service.kafka.event.PreWarmItemEvent;
import com.binhphuc.flash_sale_service.kafka.producer.PreWarmItemProducer;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PreWarmItemJob implements Job {
    private final PreWarmItemProducer preWarmItemProducer;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<String> productIds = (List<String>) jobExecutionContext.getJobDetail().getJobDataMap().get(PreWarmItemConstant.variantIdsKey);
        List<String> variantIds = (List<String>) jobExecutionContext.getJobDetail().getJobDataMap().get(PreWarmItemConstant.variantIdsKey);
        if (productIds == null || variantIds == null) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Product IDs or Variant IDs is null");
        }
        preWarmItemProducer.sendPreWarmItemEvent(PreWarmItemEvent.builder().productIds(productIds).variantIds(variantIds).build());
    }
}
