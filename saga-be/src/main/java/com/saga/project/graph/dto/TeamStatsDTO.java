package com.saga.project.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamStatsDTO {
    private int totalStudents;
    private int totalTasks;
    private int totalCommits;
    private double taskCompletionRate; // 0.0 - 100.0
    private String mostActiveStudent; // By commit count
}
