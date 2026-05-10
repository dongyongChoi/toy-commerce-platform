package com.toyproject.settlement.adapter;

import com.toyproject.order.application.event.OrderConfirmedEvent;
import com.toyproject.settlement.application.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class SettlementOrderEventListener {
    private final SettlementService settlementService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void requestSettlement(OrderConfirmedEvent event) {
        settlementService.requestSettlement(event);
    }
}
