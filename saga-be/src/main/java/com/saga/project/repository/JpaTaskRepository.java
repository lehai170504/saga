package com.saga.project.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.project.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface JpaTaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {
    Optional<Task> findByIssueKey(String issueKey);

    long countByBoardId(UUID boardId);
    
    @org.springframework.data.jpa.repository.Query("SELECT t FROM Task t WHERE t.sprintId = :sprintId AND t.status = :status")
    java.util.List<Task> findBySprintIdAndStatus(@org.springframework.data.repository.query.Param("sprintId") String sprintId, @org.springframework.data.repository.query.Param("status") String status);
    org.springframework.data.domain.Page<Task> findByBoardId(UUID boardId, org.springframework.data.domain.Pageable pageable);
}
