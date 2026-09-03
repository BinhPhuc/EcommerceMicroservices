package com.binhphuc.inventory_service.service.impl;

import com.binhphuc.common_web_starter.exception.BusinessException;
import com.binhphuc.inventory_service.dto.request.CreateProductStockRequest;
import com.binhphuc.inventory_service.dto.request.GetStockByVariantIdsRequest;
import com.binhphuc.inventory_service.dto.response.GetStockByVariantIdsResponse;
import com.binhphuc.inventory_service.entity.Inventory;
import com.binhphuc.inventory_service.repository.InventoryRepository;
import com.binhphuc.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<GetStockByVariantIdsResponse> getStockByVariantId(GetStockByVariantIdsRequest request) {
        List<Inventory> inventories = inventoryRepository.findByVariantIdIn(request.getVariantIds());
        if (inventories.size() != request.getVariantIds().size()) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Some variant IDs not found in inventory");
        }
        List<GetStockByVariantIdsResponse> responses = inventories.stream()
                .map(inventory -> GetStockByVariantIdsResponse
                        .builder()
                        .variantId(inventory.getVariantId())
                        .stock(inventory.getStock())
                        .build())
                .toList();
        return responses;
    }
}
