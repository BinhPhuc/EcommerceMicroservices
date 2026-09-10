package com.binhphuc.payment_service.repository;

import com.binhphuc.payment_service.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
    boolean existsByIdempotencyKey(String idempotencyKey);
}
