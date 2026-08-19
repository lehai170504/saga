package com.saga.academic.infrastructure.controller;
import com.saga.academic.application.dto.CreateCourseRequest;
import com.saga.academic.application.dto.CreateSemesterRequest;
import com.saga.academic.application.service.MasterDataService;
import com.saga.academic.domain.Semester;
import com.saga.shared.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/admin/academic")
public class AdminAcademicController {
    private final MasterDataService masterDataService;
    public AdminAcademicController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }
    @PostMapping("/semesters")
    public ResponseEntity<ApiResponse<Semester>> createSemester(@Valid @RequestBody CreateSemesterRequest request) {
        Semester semester = masterDataService.createSemester(request);
        return ResponseEntity.ok(ApiResponse.success(semester, "Semester created successfully"));
    }
    @PutMapping("/semesters/{semesterId}/active")
    public ResponseEntity<ApiResponse<Void>> setActiveSemester(@PathVariable UUID semesterId) {
        masterDataService.setActiveSemester(semesterId);
        return ResponseEntity.ok(ApiResponse.success(null, "Active semester updated successfully"));
    }
    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<Void>> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        masterDataService.assignCourseToLecturer(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Course assigned to lecturer successfully"));
    }
}