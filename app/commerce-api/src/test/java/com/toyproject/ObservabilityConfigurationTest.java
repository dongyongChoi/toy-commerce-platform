package com.toyproject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ObservabilityConfigurationTest {
    @Test
    @DisplayName("Logback 설정은 dev 프로필에서 Logstash Syslog appender를 사용한다")
    void logbackConfigurationContainsLogstashSyslogAppender() throws IOException {
        String logbackConfig = new String(
            new ClassPathResource("logback-spring.xml").getInputStream().readAllBytes(),
            StandardCharsets.UTF_8
        );

        assertThat(logbackConfig)
            .contains("LOGSTASH_SYSLOG")
            .contains("ch.qos.logback.classic.net.SyslogAppender")
            .contains("toy-commerce.logging.logstash.host")
            .contains("toy-commerce.logging.logstash.port")
            .contains("<springProfile name=\"dev\">");
    }

    @Test
    @DisplayName("Logstash 파이프라인은 commerce-api 로그를 Elasticsearch 인덱스로 전달한다")
    void logstashPipelineSendsCommerceApiLogsToElasticsearch() throws IOException {
        Path projectRoot = findProjectRoot();
        String pipeline = Files.readString(
            projectRoot.resolve("observability/logstash/pipeline/commerce-api.conf"),
            StandardCharsets.UTF_8
        );

        assertThat(pipeline)
            .contains("syslog")
            .contains("port => 5514")
            .contains("service.name")
            .contains("commerce-api")
            .contains("toy-commerce-logs-%{+YYYY.MM.dd}");
    }

    @Test
    @DisplayName("Docker Compose는 Elasticsearch, Logstash, Kibana 서비스를 제공한다")
    void dockerComposeContainsElkServices() throws IOException {
        Path projectRoot = findProjectRoot();
        String dockerCompose = Files.readString(projectRoot.resolve("docker-compose.yml"), StandardCharsets.UTF_8);

        assertThat(dockerCompose)
            .contains("elasticsearch:")
            .contains("logstash:")
            .contains("kibana:")
            .contains("${LOGSTASH_PORT:-5514}:5514/udp")
            .contains("./observability/logstash/pipeline:/usr/share/logstash/pipeline:ro");
    }

    private Path findProjectRoot() {
        Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (current != null && !Files.exists(current.resolve("settings.gradle"))) {
            current = current.getParent();
        }
        assertThat(current).isNotNull();
        return current;
    }
}
