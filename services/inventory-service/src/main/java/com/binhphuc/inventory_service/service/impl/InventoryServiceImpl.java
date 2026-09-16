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
import org.springframework.transaction.annotation.Transactional;

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
                .reserveStock(0L)
                .build();
        inventoryRepository.save(inventory);
    }

    @Override
    public List<GetStockByVariantIdsResponse> getStockByVariantId(GetStockByVariantIdsRequest request) {
        List<Inventory> inventories =
                inventoryRepository.findByVariantIdIn(request.getVariantIds());
        if (inventories.size() != request.getVariantIds().size()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Some variantIds not found");
        }
        return inventories.stream().map(inventory -> GetStockByVariantIdsResponse
                .builder()
                .variantId(inventory.getVariantId())
                .stock(inventory.getStock())
                .build()).toList();
    }

    @Override
    public Inventory getInventoryByVariantId(String variantId) {
        return inventoryRepository.findByVariantId(variantId).orElseThrow(() -> new BusinessException(HttpStatus.BAD_REQUEST, "VariantId not found"));
    }

    @Override
    @Transactional
    public void updateInventoryReserveStock(String variantId, Long reserveStock) {
        inventoryRepository.updateReserveStockByVariantId(variantId, reserveStock);
    }
}
