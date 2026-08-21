package com.saga.project.dto.dashboard;

import com.saga.evaluation.dto.StudentContributionDTO;
import com.saga.project.graph.dto.GraphDataDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StudentDashboardResponse {
    private StudentContributionDTO contributionSlices; // Slicing Pie Data
    private Map<String, Double> radarChartSkills; // Code, Test, Doc, Research
    private GraphDataDTO traceabilityGraph; // Neo4j Graph Data
    
    // Additional basic info
    private int totalCommits;
    private int totalTasksCompleted;
}
