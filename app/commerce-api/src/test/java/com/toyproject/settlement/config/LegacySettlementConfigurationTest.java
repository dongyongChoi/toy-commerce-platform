package com.toyproject.settlement.config;

import com.toyproject.settlement.adapter.JdbcLegacySettlementAdapter;
import com.toyproject.settlement.adapter.NoopLegacySettlementAdapter;
import com.toyproject.settlement.application.port.LegacySettlementPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

class LegacySettlementConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(TestConfiguration.class);

    @Test
    @DisplayName("레거시 정산 연동이 비활성화되면 no-op 어댑터를 사용한다")
    void legacySettlementDisabled_usesNoopAdapter() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(LegacySettlementPort.class);
            assertThat(context).hasSingleBean(NoopLegacySettlementAdapter.class);
            assertThat(context).doesNotHaveBean(JdbcLegacySettlementAdapter.class);
            assertThat(context).doesNotHaveBean("legacySettlementDataSource");
        });
    }

    @Test
    @DisplayName("레거시 정산 연동이 활성화되면 JDBC 어댑터와 전용 DataSource를 등록한다")
    void legacySettlementEnabled_registersJdbcAdapterAndDataSource() {
        contextRunner
            .withPropertyValues(
                "toy-commerce.settlement.legacy.enabled=true",
                "toy-commerce.settlement.legacy.url=jdbc:h2:mem:legacy-settlement-config-test;MODE=Oracle;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "toy-commerce.settlement.legacy.username=sa",
                "toy-commerce.settlement.legacy.password=",
                "toy-commerce.settlement.legacy.driver-class-name=org.h2.Driver"
            )
            .run(context -> {
                assertThat(context).hasSingleBean(LegacySettlementPort.class);
                assertThat(context).hasSingleBean(JdbcLegacySettlementAdapter.class);
                assertThat(context).doesNotHaveBean(NoopLegacySettlementAdapter.class);
                assertThat(context).hasBean("legacySettlementDataSource");
                assertThat(context).hasBean("legacySettlementJdbcTemplate");
                assertThat(context.getBean("legacySettlementDataSource", DataSource.class)).isNotNull();
                assertThat(context.getBean("legacySettlementJdbcTemplate", JdbcTemplate.class)).isNotNull();
            });
    }

    @EnableConfigurationProperties(LegacySettlementProperties.class)
    @Import({
        LegacySettlementDataSourceConfiguration.class,
        JdbcLegacySettlementAdapter.class,
        NoopLegacySettlementAdapter.class
    })
    static class TestConfiguration {
    }
}
