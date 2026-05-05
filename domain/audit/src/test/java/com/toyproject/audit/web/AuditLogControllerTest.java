package com.toyproject.audit.web;

import com.toyproject.audit.application.AuditLogService;
import com.toyproject.audit.domain.AuditEventType;
import com.toyproject.audit.domain.AuditLog;
import com.toyproject.common.web.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@WebMvcTest(AuditLogController.class)
@Import(GlobalExceptionHandler.class)
class AuditLogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;

    @Test
    @DisplayName("감사 로그 목록 조회 요청이 유효하면 감사 로그 목록을 반환한다")
    void getAuditLogs_returnsAuditLogResponses() throws Exception {
        AuditLog auditLog = auditLog(
            "audit-1",
            AuditEventType.ORDER_CREATED,
            "ORDER",
            "1",
            "memberId=2, productId=3, quantity=4, totalPrice=40000",
            Instant.parse("2026-05-05T10:00:00Z")
        );
        given(auditLogService.getAuditLogs(null))
            .willReturn(List.of(auditLog));

        mockMvc.perform(get("/api/v1/audit-logs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("audit-1"))
            .andExpect(jsonPath("$.data[0].eventType").value("ORDER_CREATED"))
            .andExpect(jsonPath("$.data[0].aggregateType").value("ORDER"))
            .andExpect(jsonPath("$.data[0].aggregateId").value("1"))
            .andExpect(jsonPath("$.data[0].payload").value("memberId=2, productId=3, quantity=4, totalPrice=40000"))
            .andExpect(jsonPath("$.data[0].createdAt").value("2026-05-05T10:00:00Z"));
    }

    @Test
    @DisplayName("이벤트 타입을 지정하면 필터링된 감사 로그 목록을 반환한다")
    void getAuditLogs_withEventType_returnsFilteredAuditLogResponses() throws Exception {
        AuditLog auditLog = auditLog(
            "audit-2",
            AuditEventType.ORDER_CANCELLED,
            "ORDER",
            "1",
            "memberId=2, productId=3, quantity=4, totalPrice=40000",
            Instant.parse("2026-05-05T10:10:00Z")
        );
        given(auditLogService.getAuditLogs(AuditEventType.ORDER_CANCELLED))
            .willReturn(List.of(auditLog));

        mockMvc.perform(get("/api/v1/audit-logs")
                .queryParam("eventType", "ORDER_CANCELLED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("audit-2"))
            .andExpect(jsonPath("$.data[0].eventType").value("ORDER_CANCELLED"));
    }

    @Test
    @DisplayName("지원하지 않는 이벤트 타입이면 400 응답을 반환한다")
    void getAuditLogs_withInvalidEventType_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs")
                .queryParam("eventType", "UNKNOWN"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("COMMON-400"));

        then(auditLogService).shouldHaveNoInteractions();
    }

    private AuditLog auditLog(
        String id,
        AuditEventType eventType,
        String aggregateType,
        String aggregateId,
        String payload,
        Instant createdAt
    ) {
        AuditLog auditLog = new AuditLog(eventType, aggregateType, aggregateId, payload);
        ReflectionTestUtils.setField(auditLog, "id", id);
        ReflectionTestUtils.setField(auditLog, "createdAt", createdAt);
        return auditLog;
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import(AuditLogController.class)
    static class TestApplication {
    }
}
