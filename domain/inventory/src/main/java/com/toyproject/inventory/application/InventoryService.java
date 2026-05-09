package com.toyproject.inventory.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.common.core.ErrorCode;
import com.toyproject.inventory.domain.StockItem;
import com.toyproject.inventory.domain.StockItemRepository;
import com.toyproject.inventory.web.dto.StockItemResponse;
import com.toyproject.inventory.web.dto.UpsertStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class InventoryService {
    private final StockItemRepository stockItemRepository;

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

        StockItem stockItem = stockItemRepository.findByProductIdForUpdate(productId)
            .map(existing -> {
                existing.updateQuantity(request.quantity());
                return existing;
            })
            .orElseGet(() -> new StockItem(productId, request.quantity()));

        return StockItemResponse.from(stockItemRepository.save(stockItem));
    }

    @Transactional
    public void deductStock(Long productId, int quantity) {
        StockItem stockItem = findStockItemForUpdate(productId);
        stockItem.deduct(quantity);
    }

    @Transactional
    public void restoreStock(Long productId, int quantity) {
        StockItem stockItem = findStockItemForUpdate(productId);
        stockItem.restore(quantity);
    }

    private StockItem findStockItemForUpdate(Long productId) {
        return stockItemRepository.findByProductIdForUpdate(productId)
            .orElseThrow(() -> new DomainException(ErrorCode.RESOURCE_NOT_FOUND, "재고 정보가 없습니다."));
    }
}
