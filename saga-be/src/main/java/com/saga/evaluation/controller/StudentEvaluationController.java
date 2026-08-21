package com.saga.evaluation.controller;

import com.saga.evaluation.dto.PeerReviewRequest;
import com.saga.evaluation.dto.SprintReportDTO;
import com.saga.evaluation.entity.PeerReview;
import com.saga.evaluation.service.EvaluationReportService;
import com.saga.evaluation.service.StudentEvaluationService;

import com.saga.shared.response.ApiResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/api/v1/student")
@Tag(name = "07. Student APIs")
public class StudentEvaluationController {

    private final StudentEvaluationService studentEvaluationService;
    private final EvaluationReportService evaluationReportService;

    public StudentEvaluationController(StudentEvaluationService studentEvaluationService,
            EvaluationReportService evaluationReportService) {
        this.studentEvaluationService = studentEvaluationService;
        this.evaluationReportService = evaluationReportService;
    }

    @PostMapping("/evaluation/peer-reviews")
    @Operation(summary = "Submit Peer Review")
    public ResponseEntity<ApiResponse<PeerReview>> submitPeerReview(@RequestParam UUID studentId,
            @Valid @RequestBody PeerReviewRequest request) {
        PeerReview review = studentEvaluationService.submitPeerReview(studentId, request);
        return ResponseEntity.ok(ApiResponse.success(review, "Peer review submitted successfully"));
    }

    @GetMapping("/teams/{teamId}/sprints/{sprintId}/report")
    @Operation(summary = "Get Sprint Report for Student")
    public ResponseEntity<ApiResponse<SprintReportDTO>> getSprintReport(@PathVariable UUID teamId,
            @PathVariable String sprintId, @RequestParam UUID courseId) {
        SprintReportDTO report = evaluationReportService.getSprintReport(courseId, teamId, sprintId);
        return ResponseEntity.ok(ApiResponse.success(report, "Fetched report successfully"));
    }
}

