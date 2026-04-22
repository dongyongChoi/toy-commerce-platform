package com.toyproject.inventory.web;

import com.toyproject.common.web.ApiResponse;
import com.toyproject.inventory.application.InventoryService;
import com.toyproject.inventory.web.dto.StockItemResponse;
import com.toyproject.inventory.web.dto.UpsertStockRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ApiResponse<List<StockItemResponse>> getInventory() {
        return ApiResponse.success(inventoryService.getInventory());
    }

    @PutMapping("/{productId}")
    public ApiResponse<StockItemResponse> upsertStock(
        @PathVariable Long productId,
        @Valid @RequestBody UpsertStockRequest request
    ) {
        return ApiResponse.success(inventoryService.upsertStock(productId, request), "stock updated");
    }
}

