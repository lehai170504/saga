package com.saga.project.domain;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class CommitData {
    private UUID id;
    private UUID repoId;
    private String hash;
    private String message;
    private String authorEmail;
    private String branchName;
}