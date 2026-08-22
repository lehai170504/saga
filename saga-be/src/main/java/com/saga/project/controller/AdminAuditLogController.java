package com.saga.project.controller;

import com.saga.project.entity.SystemAuditLog;
import com.saga.project.service.SystemAuditLogService;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Admin Audit Logs", description = "Endpoints for administrators to view system audit logs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAuditLogController {

    private final SystemAuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "Get paginated system audit logs")
    public ResponseEntity<ApiResponse<Page<SystemAuditLog>>> getSystemAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) UUID actorId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SystemAuditLog> logs = auditLogService.getSystemAuditLogs(pageable, actionType, actorId);

        return ResponseEntity.ok(ApiResponse.success(logs, "Fetched system audit logs successfully"));
    }
}
