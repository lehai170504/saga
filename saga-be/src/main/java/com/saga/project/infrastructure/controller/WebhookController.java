package com.saga.project.infrastructure.controller;

import com.saga.project.application.dto.GithubWebhookPayload;
import com.saga.project.application.service.TraceabilitySyncService;
import com.saga.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    private final TraceabilitySyncService syncService;

    public WebhookController(TraceabilitySyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping("/github")
    public ResponseEntity<ApiResponse<Void>> handleGithubWebhook(@RequestBody GithubWebhookPayload payload) {
        // Trigger Async processing, immediately return 202 Accepted
        syncService.handleGithubWebhook(payload);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.success(null, "Webhook received and processing started"));
    }
}
