package com.toyproject.settlement.application;

import com.toyproject.order.application.event.OrderConfirmedEvent;
import com.toyproject.settlement.application.port.LegacySettlementPort;
import com.toyproject.settlement.application.port.dto.LegacySettlementRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SettlementService {
    private final LegacySettlementPort legacySettlementPort;

    public void requestSettlement(OrderConfirmedEvent event) {
        legacySettlementPort.requestSettlement(LegacySettlementRequest.from(event));
    }
}
