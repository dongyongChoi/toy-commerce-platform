package com.toyproject.config;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerApplicationTest {
    private static final ConfigRepositoryFixture CONFIG_REPOSITORY = createConfigRepository();

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void registerConfigRepository(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.config.server.git.uri", CONFIG_REPOSITORY::uri);
        registry.add("spring.cloud.config.server.git.search-paths", () -> "config-repo");
        registry.add("spring.cloud.config.server.git.default-label", CONFIG_REPOSITORY::defaultLabel);
        registry.add("spring.cloud.config.server.git.clone-on-start", () -> "true");
    }

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

    private static ConfigRepositoryFixture createConfigRepository() {
        try {
            Path repository = Files.createTempDirectory("toy-commerce-config-repo-");
            Path configFile = repository.resolve("config-repo").resolve("commerce-api.yml");

            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, """
                toy-commerce:
                  config:
                    source: config-server
                    message: Spring Cloud Config에서 전달된 설정입니다.
                """, StandardCharsets.UTF_8);

            try (Git git = Git.init().setDirectory(repository.toFile()).call()) {
                git.add().addFilepattern(".").call();
                git.commit()
                    .setMessage("Add commerce-api configuration")
                    .setAuthor("test", "test@example.com")
                    .setCommitter("test", "test@example.com")
                    .call();

                return new ConfigRepositoryFixture(repository, git.getRepository().getBranch());
            }
        } catch (IOException | GitAPIException exception) {
            throw new IllegalStateException("Config Server 테스트용 Git 저장소 생성에 실패했습니다.", exception);
        }
    }

    private record ConfigRepositoryFixture(Path path, String defaultLabel) {
        private String uri() {
            return path.toUri().toString();
        }
    }
}
