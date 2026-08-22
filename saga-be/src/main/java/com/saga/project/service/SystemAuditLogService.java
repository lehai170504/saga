package com.saga.project.service;

import com.saga.project.entity.SystemAuditLog;
import com.saga.project.repository.JpaSystemAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SystemAuditLogService {

    private final JpaSystemAuditLogRepository auditLogRepository;

    @Async
    @Transactional
    public void logActionAsync(UUID actorId, String actionType, Map<String, Object> details) {
        try {
            SystemAuditLog auditLog = SystemAuditLog.builder()
                    .actorId(actorId)
                    .actionType(actionType)
                    .details(details)
                    .build();
            auditLogRepository.save(auditLog);
            log.debug("Audit log saved successfully for action: {}", actionType);
        } catch (Exception e) {
            log.error("Failed to save audit log for action: {}", actionType, e);
        }
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<SystemAuditLog> getSystemAuditLogs(
            org.springframework.data.domain.Pageable pageable, String actionType, UUID actorId) {
        return auditLogRepository.findAll((root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (actionType != null && !actionType.isBlank()) {
                predicates.add(cb.equal(root.get("actionType"), actionType));
            }
            if (actorId != null) {
                predicates.add(cb.equal(root.get("actorId"), actorId));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable);
    }
}
