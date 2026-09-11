package com.binhphuc.order_service.repository;

import com.binhphuc.order_service.entity.OrderOutboxVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderOutboxVariantRepository extends JpaRepository<OrderOutboxVariant, String> {
    List<OrderOutboxVariant> findAllByOutboxId(String outboxId);
}
