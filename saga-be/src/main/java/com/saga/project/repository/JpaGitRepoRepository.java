package com.saga.project.repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.project.entity.GitRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
@Repository
public interface JpaGitRepoRepository extends JpaRepository<GitRepo, UUID>, JpaSpecificationExecutor<GitRepo> {    java.util.Optional<GitRepo> findByRepoId(String repoId);

    java.util.Optional<com.saga.project.entity.GitRepo> findByTeamId(UUID projectId);
    boolean existsByTeamId(UUID projectId);
}