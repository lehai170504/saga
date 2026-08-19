package com.saga.academic.infrastructure.controller;
import com.saga.academic.application.service.CourseRosterService;
import com.saga.shared.exception.UnauthorizedException;
import com.saga.shared.response.ApiResponse;
import com.saga.user.application.port.UserRepositoryPort;
import com.saga.user.domain.User;
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
public class LecturerTeamController {
    private final CourseRosterService courseRosterService;
    private final UserRepositoryPort userRepositoryPort;

    public LecturerTeamController(CourseRosterService courseRosterService, UserRepositoryPort userRepositoryPort) {
        this.courseRosterService = courseRosterService;
        this.userRepositoryPort = userRepositoryPort;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepositoryPort.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/{courseId}/template")
    public ResponseEntity<byte[]> downloadTemplate(@PathVariable UUID courseId) {
        User user = getCurrentUser();
        byte[] excelContent = courseRosterService.downloadGroupingTemplate(courseId, user.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=team_grouping_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelContent);
    }

    @PostMapping("/{courseId}/import-teams")
    public ResponseEntity<ApiResponse<Void>> importTeams(@PathVariable UUID courseId, @RequestParam("file") MultipartFile file) {
        User user = getCurrentUser();
        courseRosterService.importTeamGrouping(courseId, user.getId(), file);
        return ResponseEntity.ok(ApiResponse.success(null, "Teams imported successfully"));
    }
}