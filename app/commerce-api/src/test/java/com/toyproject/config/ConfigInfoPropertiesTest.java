package com.toyproject.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigInfoPropertiesTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ConfigurationPropertiesAutoConfiguration.class))
        .withUserConfiguration(TestConfiguration.class);

    @Test
    @DisplayName("toy-commerce.config 설정을 ConfigInfoProperties로 바인딩한다")
    void bindsToyCommerceConfigProperties() {
        contextRunner
            .withPropertyValues(
                "toy-commerce.config.source=config-server",
                "toy-commerce.config.message=외부 설정입니다."
            )
            .run(context -> {
                ConfigInfoProperties properties = context.getBean(ConfigInfoProperties.class);

                assertThat(properties.getSource()).isEqualTo("config-server");
                assertThat(properties.getMessage()).isEqualTo("외부 설정입니다.");
            });
    }

    @Configuration
    @EnableConfigurationProperties(ConfigInfoProperties.class)
    static class TestConfiguration {
    }
}
