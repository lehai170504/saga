package com.saga.project.application.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Data
@Builder
public class TaskDTO {
    private UUID id;
    private String issueKey;
    private String status;
    private Double storyPoint;
    private List<String> labels;
}