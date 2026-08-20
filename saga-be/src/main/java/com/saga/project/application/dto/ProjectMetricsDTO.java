package com.saga.project.application.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectMetricsDTO {
    private long totalTasks;
    private long totalCommits;
    private boolean syncedJira;
    private boolean syncedGithub;
}