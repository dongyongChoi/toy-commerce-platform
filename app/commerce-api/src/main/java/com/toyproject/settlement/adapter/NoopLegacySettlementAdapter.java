package com.toyproject.settlement.adapter;

import com.toyproject.settlement.application.port.LegacySettlementPort;
import com.toyproject.settlement.application.port.dto.LegacySettlementRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "toy-commerce.settlement.legacy", name = "enabled", havingValue = "false", matchIfMissing = true)
public class NoopLegacySettlementAdapter implements LegacySettlementPort {
    @Override
    public void requestSettlement(LegacySettlementRequest request) {
        log.info(
            "레거시 정산 연동이 비활성화되어 정산 요청을 건너뜁니다. orderId={}",
            request.orderId()
        );
    }
}
