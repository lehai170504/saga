package com.saga.project.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.project.entity.TaskCommitLink;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface JpaTaskCommitLinkRepository extends JpaRepository<TaskCommitLink, UUID>, JpaSpecificationExecutor<TaskCommitLink> {}