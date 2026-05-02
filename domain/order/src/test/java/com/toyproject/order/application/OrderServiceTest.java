package com.toyproject.order.application;

import com.toyproject.common.core.DomainException;
import com.toyproject.order.application.port.MemberPort;
import com.toyproject.order.application.port.ProductPort;
import com.toyproject.order.application.port.StockPort;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private MemberPort memberPort;
    @Mock
    private ProductPort productPort;
    @Mock
    private StockPort stockPort;

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
    @DisplayName("주문 단건 조회 시 해당 주문을 반환한다")
    void getOrder_returnsOrderResponse() {
        PurchaseOrder order = new PurchaseOrder(1L, 10L, 2, BigDecimal.valueOf(20000));
        ReflectionTestUtils.setField(order, "id", 1L);
        given(purchaseOrderRepository.findById(1L)).willReturn(Optional.of(order));

        OrderResponse result = orderService.getOrder(1L);

        assertThat(result).isEqualTo(new OrderResponse(1L, 1L, 10L, 2, BigDecimal.valueOf(20000), "CREATED"));
    }

    @Test
    @DisplayName("존재하지 않는 주문 단건 조회 시 예외가 발생한다")
    void getOrder_whenNotFound_throwsDomainException() {
        given(purchaseOrderRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(99L))
            .isInstanceOf(DomainException.class)
            .hasMessage("주문을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("주문을 생성하면 재고를 차감하고 저장된 주문을 반환한다")
    void createOrder_savesOrderAndDeductsStock() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 10L, 2, BigDecimal.valueOf(20000));
        given(memberPort.exists(1L)).willReturn(true);
        given(productPort.exists(10L)).willReturn(true);
        doNothing().when(stockPort).deduct(10L, 2);
        given(purchaseOrderRepository.save(any(PurchaseOrder.class))).willAnswer(invocation -> {
            PurchaseOrder order = invocation.getArgument(0);
            ReflectionTestUtils.setField(order, "id", 1L);
            return order;
        });

        OrderResponse result = orderService.createOrder(request);

        then(stockPort).should().deduct(10L, 2);
        then(purchaseOrderRepository).should().save(orderCaptor.capture());
        PurchaseOrder savedOrder = orderCaptor.getValue();
        assertThat(savedOrder.getMemberId()).isEqualTo(1L);
        assertThat(savedOrder.getProductId()).isEqualTo(10L);
        assertThat(savedOrder.getQuantity()).isEqualTo(2);
        assertThat(savedOrder.getTotalPrice()).isEqualByComparingTo("20000");
        assertThat(savedOrder.getStatus()).isEqualTo("CREATED");
        assertThat(result).isEqualTo(new OrderResponse(1L, 1L, 10L, 2, BigDecimal.valueOf(20000), "CREATED"));
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 주문 생성 시 예외가 발생한다")
    void createOrder_whenMemberNotFound_throwsDomainException() {
        CreateOrderRequest request = new CreateOrderRequest(99L, 10L, 2, BigDecimal.valueOf(20000));
        given(memberPort.exists(99L)).willReturn(false);

        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(DomainException.class)
            .hasMessage("회원을 찾을 수 없습니다.");

        then(purchaseOrderRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 주문 생성 시 예외가 발생한다")
    void createOrder_whenProductNotFound_throwsDomainException() {
        CreateOrderRequest request = new CreateOrderRequest(1L, 99L, 2, BigDecimal.valueOf(20000));
        given(memberPort.exists(1L)).willReturn(true);
        given(productPort.exists(99L)).willReturn(false);

        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(DomainException.class)
            .hasMessage("상품을 찾을 수 없습니다.");

        then(purchaseOrderRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("CREATED 상태 주문을 확정하면 CONFIRMED 상태로 전환된다")
    void confirmOrder_transitionsToConfirmed() {
        PurchaseOrder order = new PurchaseOrder(1L, 10L, 2, BigDecimal.valueOf(20000));
        ReflectionTestUtils.setField(order, "id", 1L);
        given(purchaseOrderRepository.findById(1L)).willReturn(Optional.of(order));

        OrderResponse result = orderService.confirmOrder(1L);

        assertThat(result.status()).isEqualTo("CONFIRMED");
    }

    @Test
    @DisplayName("존재하지 않는 주문 확정 시 예외가 발생한다")
    void confirmOrder_whenNotFound_throwsDomainException() {
        given(purchaseOrderRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.confirmOrder(99L))
            .isInstanceOf(DomainException.class)
            .hasMessage("주문을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("주문을 취소하면 CANCELLED 상태로 전환되고 재고가 복구된다")
    void cancelOrder_transitionsToCancelled_andRestoresStock() {
        PurchaseOrder order = new PurchaseOrder(1L, 10L, 2, BigDecimal.valueOf(20000));
        ReflectionTestUtils.setField(order, "id", 1L);
        given(purchaseOrderRepository.findById(1L)).willReturn(Optional.of(order));
        doNothing().when(stockPort).restore(10L, 2);

        OrderResponse result = orderService.cancelOrder(1L);

        assertThat(result.status()).isEqualTo("CANCELLED");
        then(stockPort).should().restore(10L, 2);
    }

    @Test
    @DisplayName("존재하지 않는 주문 취소 시 예외가 발생한다")
    void cancelOrder_whenNotFound_throwsDomainException() {
        given(purchaseOrderRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.cancelOrder(99L))
            .isInstanceOf(DomainException.class)
            .hasMessage("주문을 찾을 수 없습니다.");
    }
}
