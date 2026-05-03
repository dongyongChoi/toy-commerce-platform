package com.toyproject;

import com.toyproject.audit.application.AuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "spring.profiles.active=dev",
    "spring.kafka.listener.auto-startup=false",
    "spring.cache.type=simple",
    "spring.datasource.url=jdbc:h2:mem:mongo-profile-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
class MongoProfileContextTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("dev 프로필이 활성화되면 감사 로그 서비스 빈을 등록한다")
    void devProfileRegistersAuditLogService() {
        AuditLogService auditLogService = applicationContext.getBean(AuditLogService.class);

        assertThat(auditLogService).isNotNull();
    }
}
