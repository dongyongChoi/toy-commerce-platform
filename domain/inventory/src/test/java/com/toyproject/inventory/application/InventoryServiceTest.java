package com.toyproject.inventory.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.inventory.domain.StockItem;
import com.toyproject.inventory.domain.StockItemRepository;
import com.toyproject.inventory.web.dto.StockItemResponse;
import com.toyproject.inventory.web.dto.UpsertStockRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock
    private StockItemRepository stockItemRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Captor
    private ArgumentCaptor<StockItem> stockItemCaptor;

    @Test
    @DisplayName("재고 목록을 조회하면 재고 응답 목록으로 변환해 반환한다")
    void getInventory_returnsMappedResponses() {
        StockItem firstItem = new StockItem(10L, 50);
        StockItem secondItem = new StockItem(20L, 30);
        ReflectionTestUtils.setField(firstItem, "id", 1L);
        ReflectionTestUtils.setField(secondItem, "id", 2L);

        given(stockItemRepository.findAll()).willReturn(List.of(firstItem, secondItem));

        List<StockItemResponse> result = inventoryService.getInventory();

        assertThat(result).containsExactly(
            new StockItemResponse(1L, 10L, 50),
            new StockItemResponse(2L, 20L, 30)
        );
    }

    @Test
    @DisplayName("기존 재고가 없으면 새 재고를 생성해 저장한다")
    void upsertStock_whenMissing_createsNewStockItem() {
        UpsertStockRequest request = new UpsertStockRequest(50);
        given(stockItemRepository.findByProductIdForUpdate(10L)).willReturn(Optional.empty());
        given(stockItemRepository.save(any(StockItem.class))).willAnswer(invocation -> {
            StockItem item = invocation.getArgument(0);
            ReflectionTestUtils.setField(item, "id", 1L);
            return item;
        });

        StockItemResponse result = inventoryService.upsertStock(10L, request);

        then(stockItemRepository).should().save(stockItemCaptor.capture());
        StockItem savedItem = stockItemCaptor.getValue();
        assertThat(savedItem.getProductId()).isEqualTo(10L);
        assertThat(savedItem.getQuantity()).isEqualTo(50);
        assertThat(result).isEqualTo(new StockItemResponse(1L, 10L, 50));
    }

    @Test
    @DisplayName("기존 재고가 있으면 수량만 갱신해 저장한다")
    void upsertStock_whenExisting_updatesQuantity() {
        StockItem existing = new StockItem(10L, 30);
        ReflectionTestUtils.setField(existing, "id", 1L);
        given(stockItemRepository.findByProductIdForUpdate(10L)).willReturn(Optional.of(existing));
        given(stockItemRepository.save(existing)).willReturn(existing);

        StockItemResponse result = inventoryService.upsertStock(10L, new UpsertStockRequest(80));

        assertThat(existing.getQuantity()).isEqualTo(80);
        assertThat(result).isEqualTo(new StockItemResponse(1L, 10L, 80));
    }

    @Test
    @DisplayName("재고 수량이 음수이면 예외가 발생한다")
    void upsertStock_withNegativeQuantity_throwsDomainException() {
        UpsertStockRequest request = new UpsertStockRequest(-1);

        assertThatThrownBy(() -> inventoryService.upsertStock(10L, request))
            .isInstanceOf(DomainException.class)
            .hasMessage("quantity must be zero or greater");

        then(stockItemRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("재고 차감 시 현재 수량에서 요청 수량만큼 차감된다")
    void deductStock_reducesQuantityByRequestedAmount() {
        StockItem stockItem = new StockItem(10L, 50);
        ReflectionTestUtils.setField(stockItem, "id", 1L);
        given(stockItemRepository.findByProductIdForUpdate(10L)).willReturn(Optional.of(stockItem));

        inventoryService.deductStock(10L, 10);

        assertThat(stockItem.getQuantity()).isEqualTo(40);
    }

    @Test
    @DisplayName("재고가 부족하면 차감 시 예외가 발생한다")
    void deductStock_whenInsufficientStock_throwsDomainException() {
        StockItem stockItem = new StockItem(10L, 5);
        given(stockItemRepository.findByProductIdForUpdate(10L)).willReturn(Optional.of(stockItem));

        assertThatThrownBy(() -> inventoryService.deductStock(10L, 10))
            .isInstanceOf(DomainException.class)
            .hasMessage("재고가 부족합니다.");
    }

    @Test
    @DisplayName("재고 정보가 없으면 차감 시 예외가 발생한다")
    void deductStock_whenStockNotFound_throwsDomainException() {
        given(stockItemRepository.findByProductIdForUpdate(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.deductStock(10L, 5))
            .isInstanceOf(DomainException.class)
            .hasMessage("재고 정보가 없습니다.");
    }

    @Test
    @DisplayName("재고 복구 시 현재 수량에 요청 수량만큼 증가한다")
    void restoreStock_increasesQuantityByRequestedAmount() {
        StockItem stockItem = new StockItem(10L, 30);
        ReflectionTestUtils.setField(stockItem, "id", 1L);
        given(stockItemRepository.findByProductIdForUpdate(10L)).willReturn(Optional.of(stockItem));

        inventoryService.restoreStock(10L, 5);

        assertThat(stockItem.getQuantity()).isEqualTo(35);
    }

    @Test
    @DisplayName("재고 정보가 없으면 복구 시 예외가 발생한다")
    void restoreStock_whenStockNotFound_throwsDomainException() {
        given(stockItemRepository.findByProductIdForUpdate(10L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.restoreStock(10L, 5))
            .isInstanceOf(DomainException.class)
            .hasMessage("재고 정보가 없습니다.");
    }
}
