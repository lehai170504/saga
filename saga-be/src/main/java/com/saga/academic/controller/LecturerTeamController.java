package com.saga.academic.controller;

import com.saga.academic.service.CourseRosterService;
import com.saga.shared.exception.UnauthorizedException;
import com.saga.shared.response.ApiResponse;
import com.saga.user.repository.JpaUserRepository;
import com.saga.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "05. Lecturer - Academic & Course APIs", description = "Endpoints for Lecturers to download templates and import team rosters via Excel")
public class LecturerTeamController {
    private final CourseRosterService courseRosterService;
    private final JpaUserRepository userRepository;

    public LecturerTeamController(CourseRosterService courseRosterService, JpaUserRepository userRepository) {
        this.courseRosterService = courseRosterService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/{courseId}/template")
    @Operation(summary = "Download Team Grouping Template", description = "Lecturer downloads an Excel template for a specific course to fill in team assignments.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Excel file downloaded successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not the lecturer of this course")
    })
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable UUID courseId) {
        User user = getCurrentUser();
        byte[] excelContent = courseRosterService.downloadGroupingTemplate(courseId, user.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=team_grouping_template.xlsx")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }

    @PostMapping(value = "/{courseId}/import-teams", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import Team Grouping", description = "Lecturer uploads the filled Excel file to map students into teams for the course.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Teams imported successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid Excel format or data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not the lecturer of this course")
    })
    public ResponseEntity<ApiResponse<Void>> importTeams(@PathVariable UUID courseId,
            @RequestParam("file") MultipartFile file) {
        User user = getCurrentUser();
        courseRosterService.importTeamGrouping(courseId, user.getId(), file);
        return ResponseEntity.ok(ApiResponse.success(null, "Teams imported successfully"));
    }
}
