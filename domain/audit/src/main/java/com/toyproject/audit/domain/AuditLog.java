package com.toyproject.audit.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "audit_logs")
public class AuditLog {
    @Id
    private String id;

    private AuditEventType eventType;
    private String aggregateType;
    private String aggregateId;
    private String payload;
    private Instant createdAt;

    public AuditLog(AuditEventType eventType, String aggregateType, String aggregateId, String payload) {
        this.eventType = eventType;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.createdAt = Instant.now();
    }
}
