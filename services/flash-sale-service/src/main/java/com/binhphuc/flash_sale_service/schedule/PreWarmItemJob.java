package com.binhphuc.flash_sale_service.schedule;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.flash_sale_service.constant.PreWarmItemConstant;
import com.binhphuc.flash_sale_service.kafka.event.dto.FlashSaleItem;
import com.binhphuc.flash_sale_service.kafka.event.PreWarmItemEvent;
import com.binhphuc.flash_sale_service.kafka.producer.PreWarmItemProducer;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PreWarmItemJob implements Job {
    private final PreWarmItemProducer preWarmItemProducer;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<FlashSaleItem> flashSaleItems = (List<FlashSaleItem>) jobExecutionContext.getJobDetail().getJobDataMap().get(PreWarmItemConstant.FLASH_SALE_ITEMS_KEY);
        if (flashSaleItems == null) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "Flash sale items is null");
        }
        preWarmItemProducer.sendPreWarmItemEvent(PreWarmItemEvent.builder().flashSaleItems(flashSaleItems).build());
    }
}
