package com.toyproject.settlement.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "toy-commerce.settlement.legacy", name = "enabled", havingValue = "true")
public class LegacySettlementDataSourceConfiguration {
    @Bean
    public DataSource legacySettlementDataSource(LegacySettlementProperties properties) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(properties.url());
        dataSource.setUsername(properties.username());
        dataSource.setPassword(properties.password());
        dataSource.setDriverClassName(properties.driverClassName());
        return dataSource;
    }

    @Bean
    public JdbcTemplate legacySettlementJdbcTemplate(
        @Qualifier("legacySettlementDataSource") DataSource dataSource
    ) {
        return new JdbcTemplate(dataSource);
    }
}
