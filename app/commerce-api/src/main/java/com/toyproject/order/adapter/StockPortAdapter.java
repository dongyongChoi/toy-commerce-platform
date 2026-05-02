package com.toyproject.order.adapter;

import com.toyproject.inventory.application.InventoryService;
import com.toyproject.order.application.port.StockPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StockPortAdapter implements StockPort {
    private final InventoryService inventoryService;

    @Override
    public void deduct(Long productId, int quantity) {
        inventoryService.deductStock(productId, quantity);
    }

    @Override
    public void restore(Long productId, int quantity) {
        inventoryService.restoreStock(productId, quantity);
    }
}
