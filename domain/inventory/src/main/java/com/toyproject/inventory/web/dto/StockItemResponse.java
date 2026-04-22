package com.toyproject.inventory.web.dto;

import com.toyproject.inventory.domain.StockItem;

public record StockItemResponse(
    Long id,
    Long productId,
    int quantity
) {
    public static StockItemResponse from(StockItem stockItem) {
        return new StockItemResponse(stockItem.getId(), stockItem.getProductId(), stockItem.getQuantity());
    }
}
