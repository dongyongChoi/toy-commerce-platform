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
    }

    private PropertySource<?> loadYamlPropertySource(String resourceName) throws IOException {
        List<PropertySource<?>> propertySources = yamlPropertySourceLoader.load(
            resourceName,
            new ClassPathResource(resourceName)
        );
        return propertySources.getFirst();
    }
}
