package com.saga.academic.controller;

import com.saga.academic.dto.CourseDTO;
import com.saga.academic.dto.TeamDetailDTO;
import com.saga.academic.service.AcademicQueryService;
import com.saga.shared.response.ApiResponse;
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
@RequestMapping("/api/v1/student/courses")
@Tag(name = "7. Student APIs", description = "Endpoints for Students to view their courses and teams")
public class StudentAcademicController {

    private final AcademicQueryService academicQueryService;
    private final JpaUserRepository userRepository;

    public StudentAcademicController(AcademicQueryService academicQueryService, JpaUserRepository userRepository) {
        this.academicQueryService = academicQueryService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));
    }


    @GetMapping
    @Operation(summary = "Get My Courses (Paginated)")
    public ResponseEntity<ApiResponse<Page<CourseDTO>>> getMyCourses(Pageable pageable, @RequestParam(required = false) String search) {
        UUID studentId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getCoursesByStudent(studentId, pageable, search), "Success"));
    }

    @GetMapping("/{courseId}/my-team")
    @Operation(summary = "Get My Team Details in Course")
    public ResponseEntity<ApiResponse<TeamDetailDTO>> getMyTeam(
            @PathVariable UUID courseId) {
        UUID studentId = getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getMyTeamInCourse(courseId, studentId), "Success"));
    }
}
