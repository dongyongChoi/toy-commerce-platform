package com.toyproject;

import com.toyproject.audit.application.AuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "spring.profiles.active=local,mongo")
class MongoProfileContextTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("mongo 프로필이 활성화되면 감사 로그 서비스 빈을 등록한다")
    void mongoProfileRegistersAuditLogService() {
        AuditLogService auditLogService = applicationContext.getBean(AuditLogService.class);

        assertThat(auditLogService).isNotNull();
    }
}
