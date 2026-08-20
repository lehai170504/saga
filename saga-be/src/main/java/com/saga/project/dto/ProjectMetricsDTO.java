package com.saga.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMetricsDTO {
    private long totalTasks;
    private long totalCommits;
    private boolean syncedJira;
    private boolean syncedGithub;
}