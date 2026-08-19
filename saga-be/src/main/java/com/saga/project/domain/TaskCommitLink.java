package com.saga.project.domain;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class TaskCommitLink {
    private UUID id;
    private UUID taskId;
    private UUID commitId;
}