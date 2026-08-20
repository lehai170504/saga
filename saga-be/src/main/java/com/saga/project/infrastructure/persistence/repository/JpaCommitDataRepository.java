package com.saga.project.infrastructure.persistence.repository;
import com.saga.project.infrastructure.persistence.entity.CommitDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface JpaCommitDataRepository extends JpaRepository<CommitDataEntity, UUID> {
    Optional<CommitDataEntity> findByHash(String hash);

    long countByRepoId(UUID repoId);
    org.springframework.data.domain.Page<CommitDataEntity> findByRepoId(UUID repoId, org.springframework.data.domain.Pageable pageable);
}