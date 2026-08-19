package com.saga.infrastructure.controller;

import com.saga.application.port.UserRepositoryPort;
import com.saga.application.service.IdentityService;
import com.saga.domain.ExternalProvider;
import com.saga.domain.IdentityMap;
import com.saga.domain.User;
import com.saga.shared.exception.UnauthorizedException;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/identities")
@Tag(name = "Identity Mapping", description = "Endpoints for linking external accounts")
public class IdentityController {
    private final IdentityService identityService;
    private final UserRepositoryPort userRepositoryPort;

    public IdentityController(IdentityService identityService, UserRepositoryPort userRepositoryPort) {
        this.identityService = identityService;
        this.userRepositoryPort = userRepositoryPort;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepositoryPort.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @DeleteMapping("/{provider}")
    public ResponseEntity<ApiResponse<Void>> unlinkIdentity(@PathVariable String provider) {
        User user = getCurrentUser();
        identityService.unlinkIdentity(user.getId(), ExternalProvider.valueOf(provider.toUpperCase()));
        return ResponseEntity.ok(ApiResponse.success(null, provider.toUpperCase() + " unlinked successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<IdentityMap>>> getMyIdentities() {
        User user = getCurrentUser();
        List<IdentityMap> identities = identityService.getIdentities(user.getId());
        return ResponseEntity.ok(ApiResponse.success(identities, "Fetched identities successfully"));
    }

    @PostMapping("/github/callback")
    public ResponseEntity<ApiResponse<String>> linkGithub(@RequestBody Map<String, String> payload) {
        User user = getCurrentUser();
        identityService.linkGithub(user.getId(), payload.get("code"));
        return ResponseEntity.ok(ApiResponse.success("Linked GitHub", "GitHub linked successfully"));
    }

    @PostMapping("/jira/callback")
    public ResponseEntity<ApiResponse<String>> linkJira(@RequestBody Map<String, String> payload) {
        User user = getCurrentUser();
        identityService.linkJira(user.getId(), payload.get("code"));
        return ResponseEntity.ok(ApiResponse.success("Linked Jira", "Jira linked successfully"));
    }
}
