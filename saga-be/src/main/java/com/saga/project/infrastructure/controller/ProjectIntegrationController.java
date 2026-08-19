package com.saga.project.infrastructure.controller;

import com.saga.project.application.service.ProjectIntegrationService;
import com.saga.project.domain.GitRepo;
import com.saga.project.domain.JiraBoard;
import com.saga.shared.exception.UnauthorizedException;
import com.saga.shared.response.ApiResponse;
import com.saga.user.application.port.UserRepositoryPort;
import com.saga.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/integrations")
public class ProjectIntegrationController {

    private final ProjectIntegrationService integrationService;
    private final UserRepositoryPort userRepositoryPort;

    public ProjectIntegrationController(ProjectIntegrationService integrationService, UserRepositoryPort userRepositoryPort) {
        this.integrationService = integrationService;
        this.userRepositoryPort = userRepositoryPort;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @GetMapping("/jira/connect")
    public ResponseEntity<ApiResponse<String>> getJiraConnectUrl(@RequestParam UUID teamId) {
        String url = integrationService.generateJiraConnectUrl(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(url, "Jira connect URL generated"));
    }

    @GetMapping("/jira/callback")
    public ResponseEntity<ApiResponse<JiraBoard>> jiraCallback(@RequestParam String code, @RequestParam String state) {
        JiraBoard board = integrationService.handleJiraCallback(getCurrentUser().getId(), code, state);
        return ResponseEntity.ok(ApiResponse.success(board, "Jira linked successfully"));
    }

    @GetMapping("/github/install")
    public ResponseEntity<ApiResponse<String>> getGithubInstallUrl(@RequestParam UUID teamId) {
        String url = integrationService.generateGithubInstallUrl(getCurrentUser().getId(), teamId);
        return ResponseEntity.ok(ApiResponse.success(url, "GitHub install URL generated"));
    }

    @PostMapping("/github/callback")
    public ResponseEntity<ApiResponse<GitRepo>> githubCallback(@RequestBody Map<String, String> payload) {
        String installationId = payload.get("installation_id");
        String state = payload.get("state");
        GitRepo repo = integrationService.handleGithubCallback(getCurrentUser().getId(), installationId, state);
        return ResponseEntity.ok(ApiResponse.success(repo, "GitHub repo linked successfully"));
    }
}
