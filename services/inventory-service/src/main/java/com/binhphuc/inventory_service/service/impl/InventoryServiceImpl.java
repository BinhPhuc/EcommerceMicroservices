package com.binhphuc.inventory_service.service.impl;

import com.binhphuc.inventory_service.dto.request.CreateProductStockRequest;
import com.binhphuc.inventory_service.entity.Inventory;
import com.binhphuc.inventory_service.repository.InventoryRepository;
import com.binhphuc.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;

    @Override
    public void createProductStock(CreateProductStockRequest request) {
        Inventory inventory = Inventory
                .builder()
                .stock(request.getStock())
                .variantId(request.getVariantId())
                .build();
        inventoryRepository.save(inventory);
    }
}
