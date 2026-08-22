package com.saga.project.controller;

import com.saga.project.dto.CommitDTO;
import com.saga.project.dto.ProjectMetricsDTO;
import com.saga.project.dto.TaskDTO;
import com.saga.project.service.ProjectDataQueryService;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/student/teams/{teamId}/project")
@Tag(name = "07. Student APIs", description = "Endpoints for Students to view the Jira/GitHub progress of their teams")
public class StudentProjectController {

        private final ProjectDataQueryService queryService;

        public StudentProjectController(ProjectDataQueryService queryService) {
                this.queryService = queryService;
        }

        @GetMapping("/metrics")
        @Operation(summary = "Get Project Metrics", description = "Fetches overall statistics (Total Tasks, Total Commits) and integration status (Jira/GitHub synced) for a specific team.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched metrics successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not a member of this team")
        })
        public ResponseEntity<ApiResponse<ProjectMetricsDTO>> getMetrics(
                        @PathVariable UUID teamId, Authentication auth) {
                UUID userId = UUID.fromString(auth.getPrincipal().toString());
                queryService.authorizeProjectAccess(userId, teamId, "STUDENT");

                return ResponseEntity.ok(ApiResponse.success(
                                queryService.getProjectMetrics(teamId),
                                "Lấy thông số thành công"));
        }

        @GetMapping("/tasks")
        @Operation(summary = "Get Team Tasks (Paginated)", description = "Fetches a paginated list of Jira tasks assigned to this team.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched tasks successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not a member of this team")
        })
        public ResponseEntity<ApiResponse<Page<TaskDTO>>> getTasks(
                        @PathVariable UUID teamId, Pageable pageable, Authentication auth) {
                UUID userId = UUID.fromString(auth.getPrincipal().toString());
                queryService.authorizeProjectAccess(userId, teamId, "STUDENT");

                return ResponseEntity.ok(ApiResponse.success(
                                queryService.getTeamTasks(teamId, pageable),
                                "Lấy danh sách task thành công"));
        }

        @GetMapping("/commits")
        @Operation(summary = "Get Team Commits (Paginated)", description = "Fetches a paginated list of GitHub commits pushed by this team.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched commits successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not a member of this team")
        })
        public ResponseEntity<ApiResponse<Page<CommitDTO>>> getCommits(
                        @PathVariable UUID teamId, Pageable pageable, Authentication auth) {
                UUID userId = UUID.fromString(auth.getPrincipal().toString());
                queryService.authorizeProjectAccess(userId, teamId, "STUDENT");

                return ResponseEntity.ok(ApiResponse.success(
                                queryService.getTeamCommits(teamId, pageable),
                                "Lấy danh sách commit thành công"));
        }

        @GetMapping("/ai-progress")
        @Operation(summary = "Get AI Progress Report", description = "Generates a markdown progress report for the team using AI.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Generated report successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not a member of this team")
        })
        public ResponseEntity<ApiResponse<String>> getAiProgressReport(
                        @PathVariable UUID teamId, Authentication auth,
                        @org.springframework.beans.factory.annotation.Autowired com.saga.project.service.AiProgressReportService aiProgressReportService) {
                UUID userId = UUID.fromString(auth.getPrincipal().toString());
                queryService.authorizeProjectAccess(userId, teamId, "STUDENT");

                String report = aiProgressReportService.generateReport(teamId);
                return ResponseEntity.ok(ApiResponse.success(report, "Tạo báo cáo tiến độ thành công"));
        }
}
