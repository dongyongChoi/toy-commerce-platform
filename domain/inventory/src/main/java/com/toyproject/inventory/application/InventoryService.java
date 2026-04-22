package com.toyproject.inventory.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import com.toyproject.inventory.domain.StockItem;
import com.toyproject.inventory.domain.StockItemRepository;
import com.toyproject.inventory.web.dto.StockItemResponse;
import com.toyproject.inventory.web.dto.UpsertStockRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InventoryService {
    private final StockItemRepository stockItemRepository;

    public InventoryService(StockItemRepository stockItemRepository) {
        this.stockItemRepository = stockItemRepository;
    }

    public List<StockItemResponse> getInventory() {
        return stockItemRepository.findAll()
            .stream()
            .map(StockItemResponse::from)
            .toList();
    }

    @Transactional
    public StockItemResponse upsertStock(Long productId, UpsertStockRequest request) {
        if (request.quantity() < 0) {
            throw new DomainException(ErrorCode.INVALID_INPUT, "quantity must be zero or greater");
        }

        StockItem stockItem = stockItemRepository.findByProductId(productId);
        if (stockItem == null) {
            stockItem = new StockItem(productId, request.quantity());
        } else {
            stockItem.updateQuantity(request.quantity());
        }

        return StockItemResponse.from(stockItemRepository.save(stockItem));
    }
}

