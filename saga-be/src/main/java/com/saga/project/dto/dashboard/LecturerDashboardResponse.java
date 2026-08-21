package com.saga.project.dto.dashboard;

import com.saga.project.graph.dto.GraphDataDTO;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class LecturerDashboardResponse {
    // SNA Graph
    private GraphDataDTO snaGraph;
    
    // Heatmap Dummy Data
    private Map<String, Integer> heatmapActivity;
    
    // Summary
    private int totalSupervisedTeams;
    private int totalEnrolledStudents;
    private int isolatedStudentsCount; // Ghosting warning
}
