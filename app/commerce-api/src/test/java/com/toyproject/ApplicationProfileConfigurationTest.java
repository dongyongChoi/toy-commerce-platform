package com.toyproject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationProfileConfigurationTest {
    private final YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();

    @Test
    @DisplayName("기본 설정은 local과 dev-config 프로필 그룹을 제공한다")
    void defaultConfigurationContainsEnvironmentProfileGroups() throws IOException {
        PropertySource<?> propertySource = loadYamlPropertySource("application.yml");

        assertThat(propertySource.getProperty("spring.profiles.default"))
            .isEqualTo("local");
        assertThat(propertySource.getProperty("spring.profiles.group.local-config[0]"))
            .isEqualTo("local");
        assertThat(propertySource.getProperty("spring.profiles.group.local-config[1]"))
            .isEqualTo("config");
        assertThat(propertySource.getProperty("spring.profiles.group.dev-config[0]"))
            .isEqualTo("dev");
        assertThat(propertySource.getProperty("spring.profiles.group.dev-config[1]"))
            .isEqualTo("config");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql[0]"))
            .isNull();
        assertThat(propertySource.getProperty("spring.cloud.config.enabled"))
            .isEqualTo(false);
        assertThat(propertySource.getProperty("toy-commerce.config.source"))
            .isEqualTo("local");
        assertThat(propertySource.getProperty("toy-commerce.config.message"))
            .isEqualTo("로컬 기본 설정입니다.");
    }

    @Test
    @DisplayName("local 프로필은 H2와 simple 캐시를 사용한다")
    void localProfileContainsH2AndSimpleCacheProperties() throws IOException {
        PropertySource<?> propertySource = loadYamlPropertySource("application-local.yml");

        assertThat(propertySource.getProperty("spring.cache.type"))
            .isEqualTo("simple");
        assertThat(propertySource.getProperty("spring.datasource.url"))
            .isEqualTo("jdbc:h2:mem:toycommerce;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        assertThat(propertySource.getProperty("spring.datasource.driver-class-name"))
            .isEqualTo("org.h2.Driver");
        assertThat(propertySource.getProperty("management.health.redis.enabled"))
            .isEqualTo(false);
    }

    @Test
    @DisplayName("dev 프로필은 외부 인프라 설정을 한 파일에서 제공한다")
    void devProfileContainsExternalInfrastructureProperties() throws IOException {
        PropertySource<?> propertySource = loadYamlPropertySource("application-dev.yml");

        assertThat(propertySource.getProperty("spring.datasource.url"))
            .isEqualTo("jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:toy_commerce}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8");
        assertThat(propertySource.getProperty("spring.datasource.driver-class-name"))
            .isEqualTo("com.mysql.cj.jdbc.Driver");
        assertThat(propertySource.getProperty("spring.datasource.username"))
            .isEqualTo("${MYSQL_USER:toy_user}");
        assertThat(propertySource.getProperty("spring.jpa.hibernate.ddl-auto"))
            .isEqualTo("update");
        assertThat(propertySource.getProperty("spring.cache.type"))
            .isEqualTo("redis");
        assertThat(propertySource.getProperty("spring.cache.redis.key-prefix"))
            .isEqualTo("toy-commerce:");
        assertThat(propertySource.getProperty("spring.data.redis.host"))
            .isEqualTo("${REDIS_HOST:localhost}");
        assertThat(propertySource.getProperty("spring.data.redis.port"))
            .isEqualTo("${REDIS_PORT:6379}");
        assertThat(propertySource.getProperty("spring.data.mongodb.uri"))
            .isEqualTo("mongodb://${MONGODB_HOST:localhost}:${MONGODB_PORT:27017}/${MONGODB_DATABASE:toy_commerce}");
        assertThat(propertySource.getProperty("spring.kafka.bootstrap-servers"))
            .isEqualTo("${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}");
        assertThat(propertySource.getProperty("spring.kafka.consumer.group-id"))
            .isEqualTo("${KAFKA_CONSUMER_GROUP_ID:toy-commerce-order-event-consumer}");
        assertThat(propertySource.getProperty("toy-commerce.kafka.topics.order-created"))
            .isEqualTo("toy-commerce.order.created");
        assertThat(propertySource.getProperty("toy-commerce.kafka.topics.order-cancelled"))
            .isEqualTo("toy-commerce.order.cancelled");
        assertThat(propertySource.getProperty("management.health.mongo.enabled"))
            .isEqualTo(true);
        assertThat(propertySource.getProperty("management.health.redis.enabled"))
            .isEqualTo(true);
    }

    @Test
    @DisplayName("config 프로필은 Config Server 클라이언트 설정을 제공한다")
    void configProfileContainsConfigServerClientProperties() throws IOException {
        PropertySource<?> propertySource = loadYamlPropertySource("application-config.yml");

        assertThat(propertySource.getProperty("spring.config.import"))
            .isEqualTo("optional:configserver:${CONFIG_SERVER_URI:http://localhost:8888}");
        assertThat(propertySource.getProperty("spring.cloud.config.enabled"))
            .isEqualTo(true);
        assertThat(propertySource.getProperty("spring.cloud.config.name"))
            .isEqualTo("commerce-api");
        assertThat(propertySource.getProperty("spring.cloud.config.fail-fast"))
            .isEqualTo(false);
    }

    private PropertySource<?> loadYamlPropertySource(String resourceName) throws IOException {
        List<PropertySource<?>> propertySources = yamlPropertySourceLoader.load(
            resourceName,
            new ClassPathResource(resourceName)
        );
        return propertySources.getFirst();
    }
}
