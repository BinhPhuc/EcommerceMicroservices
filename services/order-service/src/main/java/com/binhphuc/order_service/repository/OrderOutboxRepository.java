package com.binhphuc.order_service.repository;

import com.binhphuc.order_service.entity.OrderOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, String> {
    List<OrderOutbox> findByProcessedFalseOrderByCreatedAtAsc();
}
