package com.saga.project.infrastructure.persistence.repository;
import com.saga.project.infrastructure.persistence.entity.TaskCommitLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface JpaTaskCommitLinkRepository extends JpaRepository<TaskCommitLinkEntity, UUID> {}