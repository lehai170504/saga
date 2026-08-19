package com.saga.project.domain;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;
@Data
@Builder
public class Task {
    private UUID id;
    private UUID boardId;
    private String sprintId;
    private String issueKey;
    private List<String> labels;
}