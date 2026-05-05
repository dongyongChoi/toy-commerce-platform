package com.toyproject.audit.application;

import com.toyproject.audit.domain.AuditEventType;
import com.toyproject.audit.domain.AuditLog;
import com.toyproject.audit.domain.AuditLogRepository;
import com.toyproject.order.application.event.OrderCancelledEvent;
import com.toyproject.order.application.event.OrderCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {
    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @Captor
    private ArgumentCaptor<AuditLog> auditLogCaptor;

    @Test
    @DisplayName("주문 생성 이벤트를 감사 로그로 저장한다")
    void recordOrderCreated_savesAuditLog() {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        auditLogService.recordOrderCreated(event);

        then(auditLogRepository).should().save(auditLogCaptor.capture());
        AuditLog auditLog = auditLogCaptor.getValue();
        assertThat(auditLog.getEventType()).isEqualTo(AuditEventType.ORDER_CREATED);
        assertThat(auditLog.getAggregateType()).isEqualTo("ORDER");
        assertThat(auditLog.getAggregateId()).isEqualTo("1");
        assertThat(auditLog.getPayload()).contains("memberId=2", "productId=3", "quantity=4", "totalPrice=40000");
        assertThat(auditLog.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("주문 취소 이벤트를 감사 로그로 저장한다")
    void recordOrderCancelled_savesAuditLog() {
        OrderCancelledEvent event = new OrderCancelledEvent(1L, 2L, 3L, 4, BigDecimal.valueOf(40000));

        auditLogService.recordOrderCancelled(event);

        then(auditLogRepository).should().save(auditLogCaptor.capture());
        AuditLog auditLog = auditLogCaptor.getValue();
        assertThat(auditLog.getEventType()).isEqualTo(AuditEventType.ORDER_CANCELLED);
        assertThat(auditLog.getAggregateType()).isEqualTo("ORDER");
        assertThat(auditLog.getAggregateId()).isEqualTo("1");
        assertThat(auditLog.getPayload()).contains("memberId=2", "productId=3", "quantity=4", "totalPrice=40000");
        assertThat(auditLog.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("감사 로그 목록을 최신순으로 조회한다")
    void getAuditLogs_withoutEventType_returnsLatestAuditLogs() {
        AuditLog auditLog = new AuditLog(AuditEventType.ORDER_CREATED, "ORDER", "1", "payload");
        Sort latestFirst = Sort.by(Sort.Direction.DESC, "createdAt");
        given(auditLogRepository.findAll(latestFirst))
            .willReturn(List.of(auditLog));

        List<AuditLog> auditLogs = auditLogService.getAuditLogs(null);

        assertThat(auditLogs).containsExactly(auditLog);
        then(auditLogRepository).should().findAll(latestFirst);
    }

    @Test
    @DisplayName("이벤트 타입으로 감사 로그 목록을 필터링한다")
    void getAuditLogs_withEventType_returnsFilteredAuditLogs() {
        AuditLog auditLog = new AuditLog(AuditEventType.ORDER_CANCELLED, "ORDER", "1", "payload");
        Sort latestFirst = Sort.by(Sort.Direction.DESC, "createdAt");
        given(auditLogRepository.findByEventType(AuditEventType.ORDER_CANCELLED, latestFirst))
            .willReturn(List.of(auditLog));

        List<AuditLog> auditLogs = auditLogService.getAuditLogs(AuditEventType.ORDER_CANCELLED);

        assertThat(auditLogs).containsExactly(auditLog);
        then(auditLogRepository).should().findByEventType(AuditEventType.ORDER_CANCELLED, latestFirst);
    }
}
