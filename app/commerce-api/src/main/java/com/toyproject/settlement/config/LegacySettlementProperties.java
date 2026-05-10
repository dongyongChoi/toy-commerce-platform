package com.toyproject.settlement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toy-commerce.settlement.legacy")
public record LegacySettlementProperties(
    boolean enabled,
    String url,
    String username,
    String password,
    String driverClassName
) {
}
