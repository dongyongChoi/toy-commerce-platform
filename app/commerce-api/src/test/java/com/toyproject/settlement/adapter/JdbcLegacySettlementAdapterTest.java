package com.toyproject.settlement.adapter;

import com.toyproject.settlement.application.port.dto.LegacySettlementRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcLegacySettlementAdapterTest {
    @Test
    @DisplayName("레거시 정산 요청을 JDBC로 저장한다")
    void requestSettlement_insertsLegacySettlementRequest() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
        jdbcTemplate.execute("""
            create table legacy_settlement_requests (
                order_id bigint not null,
                member_id bigint not null,
                product_id bigint not null,
                quantity integer not null,
                total_price decimal(19, 2) not null,
                requested_at timestamp not null
            )
            """);
        JdbcLegacySettlementAdapter adapter = new JdbcLegacySettlementAdapter(jdbcTemplate);

        adapter.requestSettlement(new LegacySettlementRequest(
            1L,
            2L,
            3L,
            4,
            BigDecimal.valueOf(40000)
        ));

        Integer count = jdbcTemplate.queryForObject(
            """
                select count(*)
                from legacy_settlement_requests
                where order_id = 1
                  and member_id = 2
                  and product_id = 3
                  and quantity = 4
                  and total_price = 40000
                """,
            Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    private DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl("jdbc:h2:mem:legacy-settlement-adapter-test;MODE=Oracle;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
}
