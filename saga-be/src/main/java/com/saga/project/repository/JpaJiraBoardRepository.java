package com.saga.project.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.project.entity.JiraBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface JpaJiraBoardRepository extends JpaRepository<JiraBoard, UUID>, JpaSpecificationExecutor<JiraBoard> {
    java.util.Optional<com.saga.project.entity.JiraBoard> findByTeamId(UUID projectId);
    boolean existsByTeamId(UUID projectId);
}