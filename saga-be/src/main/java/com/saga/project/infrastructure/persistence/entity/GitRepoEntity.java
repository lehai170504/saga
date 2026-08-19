package com.saga.project.infrastructure.persistence.entity;
import com.saga.project.domain.IntegrationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "git_repos")
@Getter
@Setter
public class GitRepoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
    @Column(name = "repo_id", nullable = false)
    private String repoId;
    @Column(name = "repo_name")
    private String repoName;
    @Column(name = "repo_url")
    private String repoUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IntegrationStatus status;
    @Column(name = "linked_at")
    private LocalDateTime linkedAt;
}