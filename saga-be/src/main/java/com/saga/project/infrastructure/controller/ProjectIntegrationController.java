package com.saga.project.infrastructure.controller;

import com.saga.project.application.service.ProjectIntegrationService;
import com.saga.project.domain.GitRepo;
import com.saga.project.domain.JiraBoard;
import com.saga.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ProjectIntegrationController {
    private final ProjectIntegrationService integrationService;

    public ProjectIntegrationController(ProjectIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping("/projects/{projectId}/jira/connect")
    public ResponseEntity<ApiResponse<String>> connectJira(@PathVariable UUID projectId) {
        String authUrl = integrationService.generateJiraConnectUrl(projectId);
        return ResponseEntity.ok(ApiResponse.success(authUrl, "Jira connection URL retrieved successfully"));
    }

    @GetMapping("/integrations/jira/callback")
    public ResponseEntity<ApiResponse<JiraBoard>> jiraCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state) {
        JiraBoard board = integrationService.handleJiraCallback(code, state);
        return ResponseEntity.ok(ApiResponse.success(board, "Jira Board integrated successfully"));
    }

    @GetMapping("/projects/{projectId}/github/install")
    public ResponseEntity<ApiResponse<String>> installGithub(@PathVariable UUID projectId) {
        String installUrl = integrationService.generateGithubInstallUrl(projectId);
        return ResponseEntity.ok(ApiResponse.success(installUrl, "GitHub App installation URL retrieved successfully"));
    }

    @GetMapping("/integrations/github/callback")
    public ResponseEntity<ApiResponse<GitRepo>> githubCallback(
            @RequestParam("installation_id") String installationId,
            @RequestParam("state") String state) {
        GitRepo repo = integrationService.handleGithubCallback(installationId, state);
        return ResponseEntity.ok(ApiResponse.success(repo, "GitHub Repository integrated successfully"));
    }
}