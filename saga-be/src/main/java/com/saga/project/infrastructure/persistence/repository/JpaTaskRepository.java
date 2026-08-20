package com.saga.project.infrastructure.persistence.repository;
import com.saga.project.infrastructure.persistence.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface JpaTaskRepository extends JpaRepository<TaskEntity, UUID> {
    Optional<TaskEntity> findByIssueKey(String issueKey);

    long countByBoardId(UUID boardId);
    org.springframework.data.domain.Page<TaskEntity> findByBoardId(UUID boardId, org.springframework.data.domain.Pageable pageable);
}