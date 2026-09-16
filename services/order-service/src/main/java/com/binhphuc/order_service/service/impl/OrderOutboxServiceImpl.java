package com.binhphuc.order_service.service.impl;

import com.binhphuc.order_service.entity.OrderOutbox;
import com.binhphuc.order_service.repository.OrderOutboxRepository;
import com.binhphuc.order_service.service.OrderOutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderOutboxServiceImpl implements OrderOutboxService {
    private final OrderOutboxRepository orderOutboxRepository;

    @Override
    @Transactional
    public void setOutboxProcessed(OrderOutbox orderOutbox) {
        orderOutbox.setProcessed(true);
        orderOutboxRepository.save(orderOutbox);
    }

    @Override
    public List<OrderOutbox> getListUnprocessedOrderOutbox() {
        return orderOutboxRepository.findByProcessedFalseOrderByCreatedAtAsc();
    }
}
