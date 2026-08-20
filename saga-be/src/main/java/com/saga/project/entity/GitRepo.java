package com.saga.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "git_repos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GitRepo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID teamId;
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