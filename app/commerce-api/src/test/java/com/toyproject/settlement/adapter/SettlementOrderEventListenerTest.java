package com.toyproject.settlement.adapter;

import com.toyproject.order.application.event.OrderConfirmedEvent;
import com.toyproject.settlement.application.SettlementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class SettlementOrderEventListenerTest {
    @Mock
    private SettlementService settlementService;

    @InjectMocks
    private SettlementOrderEventListener listener;

    @Test
    @DisplayName("주문 확정 이벤트를 받으면 정산 서비스를 호출한다")
    void requestSettlement_whenOrderConfirmedEventReceived_callsSettlementService() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        listener.requestSettlement(event);

        then(settlementService).should().requestSettlement(event);
    }
}
