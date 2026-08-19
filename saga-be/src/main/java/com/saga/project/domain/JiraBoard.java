package com.saga.project.domain;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class JiraBoard {
    private UUID id;
    private UUID teamId;
    private String boardId;
    private String boardName;
    private String projectKey;
    private IntegrationStatus status;
    private LocalDateTime linkedAt;
}