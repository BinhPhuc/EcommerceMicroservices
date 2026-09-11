package com.binhphuc.order_service.service.impl;

import com.binhphuc.order_service.entity.OrderOutboxVariant;
import com.binhphuc.order_service.repository.OrderOutboxVariantRepository;
import com.binhphuc.order_service.service.OrderOutboxVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderOutboxVariantImpl implements OrderOutboxVariantService {
    private final OrderOutboxVariantRepository orderOutboxVariantRepository;

    @Override
    public List<OrderOutboxVariant> findAllByOutboxId(String outboxId) {
        return orderOutboxVariantRepository.findAllByOutboxId(outboxId);
    }
}
