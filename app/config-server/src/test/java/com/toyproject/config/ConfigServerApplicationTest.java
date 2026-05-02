package com.toyproject.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerApplicationTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Config Server는 commerce-api 외부 설정을 제공한다")
    void configServerProvidesCommerceApiConfiguration() {
        String response = restTemplate.getForObject(
            "http://localhost:" + port + "/commerce-api/default",
            String.class
        );

        assertThat(response)
            .contains("toy-commerce.config.source")
            .contains("config-server")
            .contains("toy-commerce.config.message");
    }
}
