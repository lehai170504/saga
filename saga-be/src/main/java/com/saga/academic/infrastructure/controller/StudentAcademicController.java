package com.saga.academic.infrastructure.controller;

import com.saga.academic.application.dto.CourseDTO;
import com.saga.academic.application.dto.TeamDetailDTO;
import com.saga.academic.application.service.AcademicQueryService;
import com.saga.shared.response.ApiResponse;
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
@RequestMapping("/api/v1/student/courses")
@Tag(name = "7. Student APIs", description = "Endpoints for Students to view their courses and teams")
public class StudentAcademicController {

    private final AcademicQueryService academicQueryService;
    private final UserRepositoryPort userRepositoryPort;

    public StudentAcademicController(AcademicQueryService academicQueryService, UserRepositoryPort userRepositoryPort) {
        this.academicQueryService = academicQueryService;
        this.userRepositoryPort = userRepositoryPort;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepositoryPort.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));
    }


    @GetMapping
    @Operation(summary = "Get My Courses (Paginated)")
    public ResponseEntity<ApiResponse<Page<CourseDTO>>> getMyCourses( Pageable pageable) {
        UUID studentId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getCoursesByStudent(studentId, pageable), "Success"));
    }

    @GetMapping("/{courseId}/my-team")
    @Operation(summary = "Get My Team Details in Course")
    public ResponseEntity<ApiResponse<TeamDetailDTO>> getMyTeam(
            @PathVariable UUID courseId) {
        UUID studentId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getMyTeamInCourse(courseId, studentId), "Success"));
    }
}
