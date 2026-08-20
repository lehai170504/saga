package com.saga.project.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.project.entity.CommitData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface JpaCommitDataRepository extends JpaRepository<CommitData, UUID>, JpaSpecificationExecutor<CommitData> {
    Optional<CommitData> findByHash(String hash);

    long countByRepoId(UUID repoId);
    org.springframework.data.domain.Page<CommitData> findByRepoId(UUID repoId, org.springframework.data.domain.Pageable pageable);
}