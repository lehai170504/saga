package com.saga.project.controller;

import com.saga.project.service.ProjectIntegrationService;
import com.saga.project.entity.GitRepo;
import com.saga.project.entity.JiraBoard;
import com.saga.shared.exception.UnauthorizedException;
import com.saga.shared.response.ApiResponse;
import com.saga.user.repository.JpaUserRepository;
import com.saga.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integrations")
@Tag(name = "08. Team Leader - Integrations", description = "Endpoints for Team Leaders to connect their Team to Jira and GitHub")
public class ProjectIntegrationController {

    private final ProjectIntegrationService integrationService;
    private final JpaUserRepository userRepository;

    public ProjectIntegrationController(ProjectIntegrationService integrationService, JpaUserRepository userRepository) {
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
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Jira connect URL generated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not the Team Leader")
    })
    public ResponseEntity<ApiResponse<String>> getJiraConnectUrl(@RequestParam UUID teamId) {
        String url = integrationService.generateJiraConnectUrl(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(url, "Jira connect URL generated"));
    }

    @GetMapping("/jira/callback")
    @Operation(summary = "Handle Jira OAuth Callback", description = "Jira redirects here after successful authorization with 'code' and 'state' (which contains teamId).")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Jira linked successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid code or state"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<JiraBoard>> jiraCallback(@RequestParam String code, @RequestParam String state) {
        JiraBoard board = integrationService.handleJiraCallback(getCurrentUser().getId(), code, state);
        return ResponseEntity.ok(ApiResponse.success(board, "Jira linked successfully"));
    }

    @GetMapping("/github/install")
    @Operation(summary = "Get GitHub Install URL", description = "Generates the URL to redirect the Team Leader to GitHub App Installation page. Requires teamId.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "GitHub install URL generated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden - Not the Team Leader")
    })
    public ResponseEntity<ApiResponse<String>> getGithubInstallUrl(@RequestParam UUID teamId) {
        String url = integrationService.generateGithubInstallUrl(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(url, "GitHub install URL generated"));
    }

    @PostMapping("/github/callback")
    @Operation(summary = "Handle GitHub App Callback", description = "FE calls this with 'installation_id' and 'state' after GitHub redirects back.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "GitHub repo linked successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid installation ID or state"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<GitRepo>> githubCallback(@RequestBody Map<String, String> payload) {
        String installationId = payload.get("installation_id");
        String state = payload.get("state");
        GitRepo repo = integrationService.handleGithubCallback(getCurrentUser().getId(), installationId, state);
        return ResponseEntity.ok(ApiResponse.success(repo, "GitHub repo linked successfully"));
    }
}
