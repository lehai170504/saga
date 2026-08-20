package com.saga.academic.controller;

import com.saga.academic.dto.CreateCourseRequest;
import com.saga.academic.dto.CreateSemesterRequest;
import com.saga.academic.service.MasterDataService;
import com.saga.academic.entity.Semester;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
import com.saga.academic.dto.*;
import com.saga.academic.service.AdminAcademicService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.saga.academic.service.CourseRosterService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.saga.academic.dto.SemesterDTO;
import com.saga.academic.service.AcademicQueryService;
import com.saga.academic.service.CourseService;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/academic")
@Tag(name = "4. Admin - Academic APIs", description = "Endpoints for Admin to manage Semesters and Courses")
public class AdminAcademicController {
    @GetMapping("/semesters")
    @Operation(summary = "Get Semesters (Paginated)")
    public ResponseEntity<ApiResponse<Page<SemesterDTO>>> getSemesters(Pageable pageable,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(academicQueryService.getSemesters(pageable, search), "Success"));
    }

    @GetMapping("/courses")
    @Operation(summary = "Get Courses (Paginated)")
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getCourses(Pageable pageable,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getAllCourses(pageable), "Success"));
    }

    private final MasterDataService masterDataService;
    private final AcademicQueryService academicQueryService;
    private final CourseRosterService courseRosterService;
    private final AdminAcademicService adminAcademicService;
    private final CourseService courseService;

    public AdminAcademicController(MasterDataService masterDataService, AcademicQueryService academicQueryService,
            CourseRosterService courseRosterService, AdminAcademicService adminAcademicService,
            CourseService courseService) {
        this.masterDataService = masterDataService;
        this.academicQueryService = academicQueryService;
        this.courseRosterService = courseRosterService;
        this.adminAcademicService = adminAcademicService;
        this.courseService = courseService;
    }

    @PostMapping("/semesters")
    @Operation(summary = "Create Semester", description = "Admin creates a new academic semester (e.g., Spring 2026).")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Semester created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or Invalid JWT"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<ApiResponse<Semester>> createSemester(@Valid @RequestBody CreateSemesterRequest request) {
        Semester semester = masterDataService.createSemester(request);
        return ResponseEntity.ok(ApiResponse.success(semester, "Semester created successfully"));
    }

    @PutMapping("/semesters/{semesterId}/active")
    @Operation(summary = "Set Active Semester", description = "Admin changes the currently active semester. This deactivates the previous active semester.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Active semester updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Semester not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<ApiResponse<Void>> setActiveSemester(@PathVariable UUID semesterId) {
        masterDataService.setActiveSemester(semesterId);
        return ResponseEntity.ok(ApiResponse.success(null, "Active semester updated successfully"));
    }

    @PostMapping("/courses")
    @Operation(summary = "Create Course & Assign Lecturer", description = "Admin creates a new course and assigns it to a lecturer for the current active semester.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Course created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload or duplicate"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success(courseService.createCourse(request), "Course created successfully"));
    }

    @GetMapping("/courses/{courseId}/roster-template")
    @Operation(summary = "Download Student Roster Template")
    public ResponseEntity<byte[]> downloadRosterTemplate(@PathVariable UUID courseId) {
        byte[] excelContent = courseRosterService.downloadRosterTemplate(courseId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=roster_template.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }

    @PostMapping(value = "/courses/{courseId}/import-roster", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import Student Roster")
    public ResponseEntity<ApiResponse<Void>> importRoster(@PathVariable UUID courseId,
            @RequestParam("file") MultipartFile file) {
        courseRosterService.importRoster(courseId, file);
        return ResponseEntity.ok(ApiResponse.success(null, "Roster imported successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/subjects")
    @Operation(summary = "Create Subject")
    public ResponseEntity<ApiResponse<SubjectResponse>> createSubject(@Valid @RequestBody SubjectRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminAcademicService.createSubject(request), "Subject created"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/subjects/{subjectId}")
    @Operation(summary = "Update Subject")
    public ResponseEntity<ApiResponse<SubjectResponse>> updateSubject(@PathVariable UUID subjectId,
            @Valid @RequestBody SubjectRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success(adminAcademicService.updateSubject(subjectId, request), "Subject updated"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/subjects/{subjectId}")
    @Operation(summary = "Delete Subject")
    public ResponseEntity<ApiResponse<Void>> deleteSubject(@PathVariable UUID subjectId) {
        adminAcademicService.deleteSubject(subjectId);
        return ResponseEntity.ok(ApiResponse.success(null, "Subject deleted"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/subjects")
    @Operation(summary = "Get All Subjects")
    public ResponseEntity<ApiResponse<Page<SubjectResponse>>> getAllSubjects(Pageable pageable,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(adminAcademicService.getAllSubjects(pageable, search), "Success"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/classes")
    @Operation(summary = "Create Class")
    public ResponseEntity<ApiResponse<ClassResponse>> createClass(@Valid @RequestBody ClassRequest request) {
        return ResponseEntity.ok(ApiResponse.success(adminAcademicService.createClass(request), "Class created"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/classes/{classId}")
    @Operation(summary = "Update Class")
    public ResponseEntity<ApiResponse<ClassResponse>> updateClass(@PathVariable UUID classId,
            @Valid @RequestBody ClassRequest request) {
        return ResponseEntity
                .ok(ApiResponse.success(adminAcademicService.updateClass(classId, request), "Class updated"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/classes/{classId}")
    @Operation(summary = "Delete Class")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable UUID classId) {
        adminAcademicService.deleteClass(classId);
        return ResponseEntity.ok(ApiResponse.success(null, "Class deleted"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/classes")
    @Operation(summary = "Get All Classes")
    public ResponseEntity<ApiResponse<Page<ClassResponse>>> getAllClasses(Pageable pageable,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success(adminAcademicService.getAllClasses(pageable, search), "Success"));
    }

}