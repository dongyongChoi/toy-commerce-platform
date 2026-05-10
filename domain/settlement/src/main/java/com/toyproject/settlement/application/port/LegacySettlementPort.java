package com.toyproject.settlement.application.port;

import com.toyproject.settlement.application.port.dto.LegacySettlementRequest;

public interface LegacySettlementPort {
    void requestSettlement(LegacySettlementRequest request);
}
