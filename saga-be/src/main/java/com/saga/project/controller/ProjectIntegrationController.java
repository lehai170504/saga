package com.saga.project.controller;

import com.saga.project.service.ProjectIntegrationService;
import com.saga.project.dto.*;
import com.saga.project.entity.GitRepo;
import com.saga.project.entity.JiraBoard;
import com.saga.shared.exception.UnauthorizedException;
import com.saga.shared.response.ApiResponse;
import com.saga.user.repository.JpaUserRepository;
import com.saga.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integrations")
@Tag(name = "08. Team Leader - Integrations", description = "Endpoints for Team Leaders to connect their Team to Jira and GitHub")
public class ProjectIntegrationController {

    private final ProjectIntegrationService integrationService;
    private final JpaUserRepository userRepository;

    public ProjectIntegrationController(ProjectIntegrationService integrationService,
            JpaUserRepository userRepository) {
        this.integrationService = integrationService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }


    @GetMapping("/jira/connect")
    @Operation(summary = "Get Jira Connect URL", description = "Generates the OAuth URL to redirect the Team Leader to Jira for authorization. Requires teamId.")
    public ResponseEntity<ApiResponse<String>> getJiraConnectUrl(@RequestParam UUID teamId) {
        String url = integrationService.generateJiraConnectUrl(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(url, "Jira connect URL generated"));
    }

    @GetMapping("/jira/callback")
    @Operation(summary = "Handle Jira OAuth Callback", description = "Jira redirects here after successful authorization with 'code' and 'state' (which contains teamId). Returns available Sites.")
    public ResponseEntity<ApiResponse<List<AvailableJiraSiteDTO>>> jiraCallback(@RequestParam String code,
            @RequestParam String state) {
        List<AvailableJiraSiteDTO> sites = integrationService.handleJiraCallback(getCurrentUser().getId(), code, state);
        return ResponseEntity.ok(ApiResponse.success(sites, "Jira sites fetched successfully"));
    }

    @GetMapping("/jira/projects")
    @Operation(summary = "Get Available Jira Projects", description = "Fetch projects for a selected Jira site.")
    public ResponseEntity<ApiResponse<List<AvailableJiraProjectDTO>>> getAvailableJiraProjects(
            @RequestParam UUID teamId, @RequestParam String siteId) {
        List<AvailableJiraProjectDTO> projects = integrationService.getAvailableJiraProjects(getCurrentUser().getId(),
                teamId, siteId);
        return ResponseEntity.ok(ApiResponse.success(projects, "Jira projects fetched successfully"));
    }

    @PostMapping("/jira/confirm")
    @Operation(summary = "Confirm Jira Project Selection", description = "Link the team to the selected Jira project.")
    public ResponseEntity<ApiResponse<JiraBoard>> confirmJiraProject(@RequestParam UUID teamId,
            @RequestBody JiraConfirmRequest request) {
        JiraBoard board = integrationService.confirmJiraProject(getCurrentUser().getId(), teamId, request);
        return ResponseEntity.ok(ApiResponse.success(board, "Jira project linked successfully"));
    }

    @PostMapping("/jira/sync")
    @Operation(summary = "Manual Sync Jira", description = "Trigger a manual sync of Jira tasks.")
    public ResponseEntity<ApiResponse<Void>> manualSyncJira(@RequestParam UUID teamId) {
        integrationService.triggerManualJiraSync(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(null, "Manual Jira sync triggered successfully"));
    }

    @DeleteMapping("/jira")
    @Operation(summary = "Unlink Jira", description = "Unlink Jira project from the team.")
    public ResponseEntity<ApiResponse<Void>> unlinkJira(@RequestParam UUID teamId) {
        integrationService.unlinkJira(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(null, "Jira unlinked successfully"));
    }


    @GetMapping("/github/install")
    @Operation(summary = "Get GitHub Install URL", description = "Generates the URL to redirect the Team Leader to GitHub App Installation page. Requires teamId.")
    public ResponseEntity<ApiResponse<String>> getGithubInstallUrl(@RequestParam UUID teamId) {
        String url = integrationService.generateGithubInstallUrl(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(url, "GitHub install URL generated"));
    }

    @PostMapping("/github/callback")
    @Operation(summary = "Handle GitHub App Callback", description = "FE calls this with 'installation_id' and 'state' after GitHub redirects back. Returns available repos.")
    public ResponseEntity<ApiResponse<List<AvailableGithubRepoDTO>>> githubCallback(
            @RequestBody Map<String, String> payload) {
        String installationId = payload.get("installation_id");
        String state = payload.get("state");
        List<AvailableGithubRepoDTO> repos = integrationService.handleGithubCallback(getCurrentUser().getId(),
                installationId, state);
        return ResponseEntity.ok(ApiResponse.success(repos, "GitHub repos fetched successfully"));
    }

    @PostMapping("/github/confirm")
    @Operation(summary = "Confirm GitHub Repos Selection", description = "Link the team to the selected GitHub repos.")
    public ResponseEntity<ApiResponse<List<GitRepo>>> confirmGithubRepos(@RequestParam UUID teamId,
            @RequestBody GithubConfirmRequest request) {
        List<GitRepo> repos = integrationService.confirmGithubRepos(getCurrentUser().getId(), teamId, request);
        return ResponseEntity.ok(ApiResponse.success(repos, "GitHub repos linked successfully"));
    }

    @PostMapping("/github/sync")
    @Operation(summary = "Manual Sync GitHub", description = "Trigger a manual sync of GitHub commits.")
    public ResponseEntity<ApiResponse<Void>> manualSyncGithub(@RequestParam UUID teamId) {
        integrationService.triggerManualGithubSync(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(null, "Manual GitHub sync triggered successfully"));
    }

    @DeleteMapping("/github")
    @Operation(summary = "Unlink GitHub", description = "Unlink all GitHub repos from the team.")
    public ResponseEntity<ApiResponse<Void>> unlinkGithub(@RequestParam UUID teamId) {
        integrationService.unlinkGithub(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(null, "GitHub unlinked successfully"));
    }
}

