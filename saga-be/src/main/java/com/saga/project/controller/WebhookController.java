package com.saga.project.controller;

import com.saga.project.dto.GithubWebhookPayload;
import com.saga.project.dto.JiraWebhookPayload;
import com.saga.project.service.TraceabilitySyncService;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
@Tag(name = "09. Webhooks", description = "System endpoints for receiving events from external providers (GitHub, Jira)")
public class WebhookController {

    private final TraceabilitySyncService syncService;

    public WebhookController(TraceabilitySyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/jira")
    @Operation(summary = "Jira Webhook Receiver", description = "Listens for events from Jira to sync Tasks. Not meant to be called by FE.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Webhook received and processing started"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    public ResponseEntity<ApiResponse<Void>> handleJiraWebhook(@RequestBody JiraWebhookPayload payload) {
        syncService.handleJiraWebhook(payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(null, "Jira Webhook received and processing started"));
    }

    @PostMapping("/github")
    @Operation(summary = "GitHub Webhook Receiver", description = "Listens for 'push' events from GitHub to sync Commits. Not meant to be called by FE.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Webhook received and processing started"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload")
    })
    public ResponseEntity<ApiResponse<Void>> handleGithubWebhook(@RequestBody GithubWebhookPayload payload) {
        syncService.handleGithubWebhook(payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(null, "Webhook received and processing started"));
    }
}
