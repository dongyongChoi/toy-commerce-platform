package com.toyproject.audit.web;

import com.toyproject.audit.application.AuditLogService;
import com.toyproject.audit.domain.AuditEventType;
import com.toyproject.audit.web.dto.AuditLogResponse;
import com.toyproject.common.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile("dev")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {
    private final AuditLogService auditLogService;

    @GetMapping
    public ApiResponse<List<AuditLogResponse>> getAuditLogs(
        @RequestParam(required = false) AuditEventType eventType
    ) {
        List<AuditLogResponse> auditLogs = auditLogService.getAuditLogs(eventType)
            .stream()
            .map(AuditLogResponse::from)
            .toList();
        return ApiResponse.success(auditLogs);
    }
}
