package com.saga.project.application.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
public class CommitDTO {
    private UUID id;
    private String hash;
    private String message;
    private String authorEmail;
    private String branchName;
    private LocalDateTime createdAt;
}