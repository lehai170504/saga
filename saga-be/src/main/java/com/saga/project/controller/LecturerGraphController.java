package com.saga.project.controller;

import com.saga.project.graph.dto.GraphDataDTO;
import com.saga.project.graph.dto.TeamStatsDTO;
import com.saga.project.service.TraceabilityGraphService;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lecturer")
@Tag(name = "11. Lecturer - Graph Analytics", description = "Endpoints for Lecturers to visualize Traceability Graphs and Charts")
public class LecturerGraphController {

    private final TraceabilityGraphService graphService;

    public LecturerGraphController(TraceabilityGraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/teams/{teamId}/sprints/{sprintId}/graph")
    @Operation(summary = "Get Traceability Graph Data", description = "Returns Nodes and Edges for Cytoscape.js rendering")
    public ResponseEntity<ApiResponse<GraphDataDTO>> getGraphData(@PathVariable UUID teamId,
            @PathVariable String sprintId) {
        GraphDataDTO data = graphService.getGraphData(teamId, sprintId);
        return ResponseEntity.ok(ApiResponse.success(data, "Fetched graph data successfully"));
    }

    @GetMapping("/teams/{teamId}/sprints/{sprintId}/stats")
    @Operation(summary = "Get Team Statistics for Charts", description = "Returns aggregated data for Pie/Bar charts")
    public ResponseEntity<ApiResponse<TeamStatsDTO>> getTeamStats(@PathVariable UUID teamId,
            @PathVariable String sprintId) {
        TeamStatsDTO stats = graphService.getTeamStats(teamId, sprintId);
        return ResponseEntity.ok(ApiResponse.success(stats, "Fetched team stats successfully"));
    }
}
