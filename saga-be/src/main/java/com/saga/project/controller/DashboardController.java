package com.saga.project.controller;

import com.saga.project.dto.dashboard.AdminDashboardResponse;
import com.saga.project.dto.dashboard.LecturerDashboardResponse;
import com.saga.project.dto.dashboard.StudentDashboardResponse;
import com.saga.project.service.DashboardService;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "10. Dashboards", description = "Role-based Dashboards (SG-6)")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Admin Dashboard Metrics")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getAdminDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getAdminDashboard(), "Fetched admin dashboard"));
    }

    @GetMapping("/lecturer/{lecturerId}")
    @PreAuthorize("hasAnyRole('LECTURER', 'ADMIN')")
    @Operation(summary = "Get Lecturer Dashboard Metrics")
    public ResponseEntity<ApiResponse<LecturerDashboardResponse>> getLecturerDashboard(@PathVariable UUID lecturerId) {
        return ResponseEntity.ok(
                ApiResponse.success(dashboardService.getLecturerDashboard(lecturerId), "Fetched lecturer dashboard"));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'LECTURER', 'ADMIN')")
    @Operation(summary = "Get Student Dashboard Metrics")
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getStudentDashboard(@PathVariable UUID studentId,
            @RequestParam(required = false) UUID courseId) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getStudentDashboard(studentId, courseId),
                "Fetched student dashboard"));
    }
}
