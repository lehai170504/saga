package com.saga.evaluation.controller;

import com.saga.evaluation.dto.OverrideRequest;
import com.saga.evaluation.dto.SprintReportDTO;
import com.saga.evaluation.dto.TaskWeightBatchRequest;
import com.saga.evaluation.entity.ContributionOverride;
import com.saga.evaluation.service.EvaluationConfigService;
import com.saga.evaluation.service.EvaluationReportService;
import com.saga.evaluation.service.OverrideService;
import com.saga.shared.response.ApiResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lecturer")
@Tag(name = "10. Lecturer - Evaluation APIs", description = "Endpoints for Lecturers to configure weights, calculate Slicing Pie, and override points")
public class LecturerEvaluationController {

    private final EvaluationConfigService evaluationConfigService;
    private final EvaluationReportService evaluationReportService;
    private final OverrideService overrideService;

    public LecturerEvaluationController(EvaluationConfigService evaluationConfigService,
            EvaluationReportService evaluationReportService,
            OverrideService overrideService) {
        this.evaluationConfigService = evaluationConfigService;
        this.evaluationReportService = evaluationReportService;
        this.overrideService = overrideService;
    }

    @PutMapping("/courses/{courseId}/evaluation/weights")
    @Operation(summary = "Update Evaluation Weights for a Course")
    public ResponseEntity<ApiResponse<Void>> updateCourseWeights(@PathVariable UUID courseId,
            @Valid @RequestBody TaskWeightBatchRequest request) {
        evaluationConfigService.saveCourseWeights(courseId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Course weights updated successfully"));
    }

    @PutMapping("/teams/{teamId}/evaluation/weights")
    @Operation(summary = "Update Evaluation Weights for a Team")
    public ResponseEntity<ApiResponse<Void>> updateTeamWeights(@PathVariable UUID teamId, @RequestParam UUID courseId,
            @Valid @RequestBody TaskWeightBatchRequest request) {
        evaluationConfigService.saveTeamWeights(courseId, teamId, request);
        return ResponseEntity.ok(ApiResponse.success(null, "Team weights updated successfully"));
    }

    @PostMapping("/sprints/{sprintId}/students/{studentId}/override")
    @Operation(summary = "Override Student Contribution Percentage")
    public ResponseEntity<ApiResponse<ContributionOverride>> overrideStudentContribution(@PathVariable String sprintId,
            @PathVariable UUID studentId, @RequestParam UUID lecturerId, @Valid @RequestBody OverrideRequest request) {
        ContributionOverride override = overrideService.overrideStudentContribution(sprintId, studentId, lecturerId,
                request);
        return ResponseEntity.ok(ApiResponse.success(override, "Override applied successfully"));
    }

    @GetMapping("/teams/{teamId}/sprints/{sprintId}/report")
    @Operation(summary = "Get Sprint Report for Lecturer")
    public ResponseEntity<ApiResponse<SprintReportDTO>> getSprintReport(@PathVariable UUID teamId,
            @PathVariable String sprintId, @RequestParam UUID courseId) {

        SprintReportDTO report = evaluationReportService.getSprintReport(courseId, teamId, sprintId);
        return ResponseEntity.ok(ApiResponse.success(report, "Fetched report successfully"));
    }
}
