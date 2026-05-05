package com.toyproject.audit.web.dto;

import com.toyproject.audit.domain.AuditEventType;
import com.toyproject.audit.domain.AuditLog;

import java.time.Instant;

public record AuditLogResponse(
    String id,
    AuditEventType eventType,
    String aggregateType,
    String aggregateId,
    String payload,
    Instant createdAt
) {
    public static AuditLogResponse from(AuditLog auditLog) {
        return new AuditLogResponse(
            auditLog.getId(),
            auditLog.getEventType(),
            auditLog.getAggregateType(),
            auditLog.getAggregateId(),
            auditLog.getPayload(),
            auditLog.getCreatedAt()
        );
    }
}
