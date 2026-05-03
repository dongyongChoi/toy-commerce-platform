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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
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
}
