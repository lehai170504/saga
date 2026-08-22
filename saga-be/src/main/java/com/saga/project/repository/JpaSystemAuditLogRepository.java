package com.saga.project.repository;

import com.saga.project.entity.SystemAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface JpaSystemAuditLogRepository
        extends JpaRepository<SystemAuditLog, UUID>, JpaSpecificationExecutor<SystemAuditLog> {
    List<SystemAuditLog> findTop10ByOrderByCreatedAtDesc();
}
