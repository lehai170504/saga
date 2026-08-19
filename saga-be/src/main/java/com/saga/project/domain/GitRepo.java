package com.saga.project.domain;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class GitRepo {
    private UUID id;
    private UUID teamId;
    private String repoId;
    private String repoName;
    private String repoUrl;
    private IntegrationStatus status;
    private LocalDateTime linkedAt;
}