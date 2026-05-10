package com.toyproject.settlement.adapter;

import com.toyproject.settlement.application.port.LegacySettlementPort;
import com.toyproject.settlement.application.port.dto.LegacySettlementRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "toy-commerce.settlement.legacy", name = "enabled", havingValue = "true")
public class JdbcLegacySettlementAdapter implements LegacySettlementPort {
    private static final String INSERT_SETTLEMENT_REQUEST = """
        insert into legacy_settlement_requests
            (order_id, member_id, product_id, quantity, total_price, requested_at)
        values (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcLegacySettlementAdapter(
        @Qualifier("legacySettlementJdbcTemplate") JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void requestSettlement(LegacySettlementRequest request) {
        jdbcTemplate.update(
            INSERT_SETTLEMENT_REQUEST,
            request.orderId(),
            request.memberId(),
            request.productId(),
            request.quantity(),
            request.totalPrice()
        );
    }
}
