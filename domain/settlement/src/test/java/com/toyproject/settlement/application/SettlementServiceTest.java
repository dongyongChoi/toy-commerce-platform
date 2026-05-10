package com.toyproject.settlement.application;

import com.toyproject.order.application.event.OrderConfirmedEvent;
import com.toyproject.settlement.application.port.LegacySettlementPort;
import com.toyproject.settlement.application.port.dto.LegacySettlementRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {
    @Mock
    private LegacySettlementPort legacySettlementPort;

    @InjectMocks
    private SettlementService settlementService;

    @Captor
    private ArgumentCaptor<LegacySettlementRequest> requestCaptor;

    @Test
    @DisplayName("주문 확정 이벤트를 레거시 정산 요청으로 변환해 전달한다")
    void requestSettlement_sendsLegacySettlementRequest() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        settlementService.requestSettlement(event);

        then(legacySettlementPort).should().requestSettlement(requestCaptor.capture());
        LegacySettlementRequest request = requestCaptor.getValue();
        assertThat(request.orderId()).isEqualTo(1L);
        assertThat(request.memberId()).isEqualTo(2L);
        assertThat(request.productId()).isEqualTo(3L);
        assertThat(request.quantity()).isEqualTo(4);
        assertThat(request.totalPrice()).isEqualByComparingTo("40000");
    }
}
