package com.binhphuc.flash_sale_service.schedule;

import com.binhphuc.flash_sale_service.service.OrderOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderOutboxRecoveryTask {
    private final OrderOutboxService orderOutboxService;

    @Scheduled(fixedDelayString = "${flash-sale.outbox.recovery-delay:10000}")
    public void recoverPendingRecords() {
        try {
            orderOutboxService.recoverPendingRecords();
        } catch (Exception e) {
            log.error("Failed to recover pending outbox records", e);
        }
    }
}
