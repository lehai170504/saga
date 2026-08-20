package com.saga.academic.infrastructure.controller;

import com.saga.academic.application.dto.CourseDTO;
import com.saga.academic.application.dto.TeamDTO;
import com.saga.academic.application.service.AcademicQueryService;
import com.saga.shared.response.ApiResponse;
import com.saga.user.application.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.saga.user.application.port.UserRepositoryPort;
import com.saga.user.domain.User;
import com.saga.shared.exception.UnauthorizedException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lecturer/courses")
@Tag(name = "5. Lecturer - Academic & Course APIs", description = "Endpoints for Lecturers to manage their assigned courses")
public class LecturerAcademicController {

    private final AcademicQueryService academicQueryService;
    private final UserRepositoryPort userRepositoryPort;

    public LecturerAcademicController(AcademicQueryService academicQueryService,
            UserRepositoryPort userRepositoryPort) {
        this.academicQueryService = academicQueryService;
        this.userRepositoryPort = userRepositoryPort;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepositoryPort.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping
    @Operation(summary = "Get My Assigned Courses (Paginated)")
    public ResponseEntity<ApiResponse<Page<CourseDTO>>> getMyCourses( Pageable pageable) {
        UUID lecturerId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getCoursesByLecturer(lecturerId, pageable), "Success"));
    }

    @GetMapping("/{courseId}/students")
    @Operation(summary = "Get Course Students (Paginated)")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getCourseStudents(
            @PathVariable UUID courseId, Pageable pageable) {
        UUID lecturerId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getCourseStudents(courseId, lecturerId, pageable), "Success"));
    }

    @GetMapping("/{courseId}/teams")
    @Operation(summary = "Get Course Teams (Paginated)")
    public ResponseEntity<ApiResponse<Page<TeamDTO>>> getCourseTeams(
            @PathVariable UUID courseId, Pageable pageable) {
        UUID lecturerId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getCourseTeams(courseId, lecturerId, pageable), "Success"));
    }
}
