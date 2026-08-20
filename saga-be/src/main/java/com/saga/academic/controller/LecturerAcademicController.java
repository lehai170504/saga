package com.saga.academic.controller;

import com.saga.academic.dto.CourseDTO;
import com.saga.academic.dto.TeamDTO;
import com.saga.academic.service.AcademicQueryService;
import com.saga.shared.response.ApiResponse;
import com.saga.user.dto.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.saga.user.repository.JpaUserRepository;
import com.saga.user.entity.User;
import com.saga.shared.exception.UnauthorizedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lecturer/courses")
@Tag(name = "5. Lecturer - Academic & Course APIs", description = "Endpoints for Lecturers to manage their assigned courses")
public class LecturerAcademicController {

    private final AcademicQueryService academicQueryService;
    private final JpaUserRepository userRepository;

    public LecturerAcademicController(AcademicQueryService academicQueryService,
            JpaUserRepository userRepository) {
        this.academicQueryService = academicQueryService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping
    @Operation(summary = "Get My Assigned Courses (Paginated)")
    public ResponseEntity<ApiResponse<Page<CourseDTO>>> getMyCourses(Pageable pageable, @RequestParam(required = false) String search) {
        UUID lecturerId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getCoursesByLecturer(lecturerId, pageable, search), "Success"));
    }

    @GetMapping("/{courseId}/students")
    @Operation(summary = "Get Course Students (Paginated)")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getCourseStudents(
            @PathVariable UUID courseId, Pageable pageable, @RequestParam(required = false) String search) {
        UUID lecturerId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getCourseStudents(courseId, lecturerId, pageable, search), "Success"));
    }

    @GetMapping("/{courseId}/teams")
    @Operation(summary = "Get Course Teams (Paginated)")
    public ResponseEntity<ApiResponse<Page<TeamDTO>>> getCourseTeams(
            @PathVariable UUID courseId, Pageable pageable, @RequestParam(required = false) String search) {
        UUID lecturerId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getCourseTeams(courseId, lecturerId, pageable, search), "Success"));
    }
}
