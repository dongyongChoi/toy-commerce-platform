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
    @DisplayName("기본 설정은 조합형 로컬 프로필 그룹을 제공한다")
    void defaultConfigurationContainsComposableLocalProfileGroups() throws IOException {
        PropertySource<?> propertySource = loadYamlPropertySource("application.yml");

        assertThat(propertySource.getProperty("spring.profiles.default"))
            .isEqualTo("local");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql[0]"))
            .isEqualTo("local");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql[1]"))
            .isEqualTo("mysql");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-redis[0]"))
            .isEqualTo("local");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-redis[1]"))
            .isEqualTo("mysql");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-redis[2]"))
            .isEqualTo("redis");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-kafka[0]"))
            .isEqualTo("local");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-kafka[1]"))
            .isEqualTo("mysql");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-kafka[2]"))
            .isEqualTo("kafka");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-redis-kafka[0]"))
            .isEqualTo("local");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-redis-kafka[1]"))
            .isEqualTo("mysql");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-redis-kafka[2]"))
            .isEqualTo("redis");
        assertThat(propertySource.getProperty("spring.profiles.group.local-mysql-redis-kafka[3]"))
            .isEqualTo("kafka");
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
    @DisplayName("mysql 프로필은 MySQL 데이터소스 설정을 제공한다")
    void mysqlProfileContainsMysqlDataSourceProperties() throws IOException {
        PropertySource<?> propertySource = loadYamlPropertySource("application-mysql.yml");

        assertThat(propertySource.getProperty("spring.datasource.url"))
            .isEqualTo("jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:toy_commerce}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8");
        assertThat(propertySource.getProperty("spring.datasource.driver-class-name"))
            .isEqualTo("com.mysql.cj.jdbc.Driver");
        assertThat(propertySource.getProperty("spring.datasource.username"))
            .isEqualTo("${MYSQL_USER:toy_user}");
        assertThat(propertySource.getProperty("spring.jpa.hibernate.ddl-auto"))
            .isEqualTo("update");
        assertThat(propertySource.getProperty("spring.cache.type"))
            .isNull();
    }

    @Test
    @DisplayName("redis 프로필은 Redis 캐시 설정을 제공한다")
    void redisProfileContainsRedisCacheProperties() throws IOException {
        PropertySource<?> propertySource = loadYamlPropertySource("application-redis.yml");

        assertThat(propertySource.getProperty("spring.cache.type"))
            .isEqualTo("redis");
        assertThat(propertySource.getProperty("spring.cache.redis.cache-null-values"))
            .isEqualTo(false);
        assertThat(propertySource.getProperty("spring.cache.redis.key-prefix"))
            .isEqualTo("toy-commerce:");
        assertThat(propertySource.getProperty("spring.data.redis.host"))
            .isEqualTo("${REDIS_HOST:localhost}");
        assertThat(propertySource.getProperty("spring.data.redis.port"))
            .isEqualTo("${REDIS_PORT:6379}");
        assertThat(propertySource.getProperty("management.health.redis.enabled"))
            .isEqualTo(true);
    }

    @Test
    @DisplayName("kafka 프로필은 Kafka Producer와 주문 이벤트 토픽 설정을 제공한다")
    void kafkaProfileContainsKafkaProducerAndTopicProperties() throws IOException {
        PropertySource<?> propertySource = loadYamlPropertySource("application-kafka.yml");

        assertThat(propertySource.getProperty("spring.kafka.bootstrap-servers"))
            .isEqualTo("${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}");
        assertThat(propertySource.getProperty("spring.kafka.producer.key-serializer"))
            .isEqualTo("org.apache.kafka.common.serialization.StringSerializer");
        assertThat(propertySource.getProperty("spring.kafka.producer.value-serializer"))
            .isEqualTo("org.springframework.kafka.support.serializer.JsonSerializer");
        assertThat(propertySource.getProperty("toy-commerce.kafka.topics.order-created"))
            .isEqualTo("toy-commerce.order.created");
        assertThat(propertySource.getProperty("toy-commerce.kafka.topics.order-cancelled"))
            .isEqualTo("toy-commerce.order.cancelled");
    }

    private PropertySource<?> loadYamlPropertySource(String resourceName) throws IOException {
        List<PropertySource<?>> propertySources = yamlPropertySourceLoader.load(
            resourceName,
            new ClassPathResource(resourceName)
        );
        return propertySources.getFirst();
    }
}
