package com.toyproject.order.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.order.domain.PurchaseOrder;
import com.toyproject.order.domain.PurchaseOrderRepository;
import com.toyproject.order.web.dto.CreateOrderRequest;
import com.toyproject.order.web.dto.OrderResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<PurchaseOrder> orderCaptor;

    @Test
    @DisplayName("주문 목록을 조회하면 주문 응답 목록으로 변환해 반환한다")
    void getOrders_returnsMappedResponses() {
        PurchaseOrder firstOrder = new PurchaseOrder(1L, 10L, 2, BigDecimal.valueOf(20000));
        PurchaseOrder secondOrder = new PurchaseOrder(2L, 20L, 1, BigDecimal.valueOf(5000));
        ReflectionTestUtils.setField(firstOrder, "id", 1L);
        ReflectionTestUtils.setField(secondOrder, "id", 2L);

        given(purchaseOrderRepository.findAll()).willReturn(List.of(firstOrder, secondOrder));

        List<OrderResponse> result = orderService.getOrders();

        assertThat(result).containsExactly(
            new OrderResponse(1L, 1L, 10L, 2, BigDecimal.valueOf(20000), "CREATED"),
            new OrderResponse(2L, 2L, 20L, 1, BigDecimal.valueOf(5000), "CREATED")
        );
    }

    @Test
    @DisplayName("주문을 생성하면 저장된 주문 응답을 반환한다")
    void createOrder_savesOrderAndReturnsResponse() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 10L, 2, BigDecimal.valueOf(20000));
        given(purchaseOrderRepository.save(any(PurchaseOrder.class))).willAnswer(invocation -> {
            PurchaseOrder order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 1L);
            return order;
        });

        OrderResponse result = orderService.createOrder(request);

        then(purchaseOrderRepository).should().save(orderCaptor.capture());
        PurchaseOrder savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getMemberId()).isEqualTo(1L);
        assertThat(savedOrder.getProductId()).isEqualTo(10L);
        assertThat(savedOrder.getQuantity()).isEqualTo(2);
        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo("20000");
        assertThat(savedOrder.getStatus()).isEqualTo("CREATED");
        assertThat(result).isEqualTo(
            new OrderResponse(1L, 1L, 10L, 2, BigDecimal.valueOf(20000), "CREATED")
        );
    }

    @Test
    @DisplayName("주문 수량이 0 이하이면 예외가 발생한다")
    void createOrder_withNonPositiveQuantity_throwsDomainException() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 10L, 0, BigDecimal.valueOf(20000));

        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(DomainException.class)
            .hasMessage("quantity must be greater than zero");

        then(purchaseOrderRepository).shouldHaveNoInteractions();
    }
}
