package com.saga.identity.controller;

import com.saga.user.repository.JpaUserRepository;
import com.saga.identity.service.IdentityService;
import com.saga.identity.entity.ExternalProvider;
import com.saga.identity.entity.IdentityMap;
import com.saga.user.entity.User;
import com.saga.shared.exception.UnauthorizedException;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/identities")
@Tag(name = "2. Identity Mapping APIs", description = "Endpoints for linking/unlinking external accounts (GitHub, Jira)")
public class IdentityController {
    private final IdentityService identityService;
    private final JpaUserRepository userRepository;

    public IdentityController(IdentityService identityService, JpaUserRepository userRepository) {
        this.identityService = identityService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        return userRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));
    }

    @DeleteMapping("/{provider}")
    @Operation(summary = "Unlink Identity", description = "Unlink an external identity (e.g., GITHUB or JIRA) from the current user account.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Unlinked successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid provider name"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or Invalid JWT")
    })
    public ResponseEntity<ApiResponse<Void>> unlinkIdentity(@PathVariable String provider) {
        User user = getCurrentUser();
        identityService.unlinkIdentity(user.getId(), ExternalProvider.valueOf(provider.toUpperCase()));
        return ResponseEntity.ok(ApiResponse.success(null, provider.toUpperCase() + " unlinked successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get Linked Identities", description = "Get a list of all external identities (Jira, GitHub) linked to the current user.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fetched identities successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or Invalid JWT")
    })
    public ResponseEntity<ApiResponse<List<IdentityMap>>> getMyIdentities() {
        User user = getCurrentUser();
        List<IdentityMap> identities = identityService.getIdentities(user.getId());
        return ResponseEntity.ok(ApiResponse.success(identities, "Fetched identities successfully"));
    }

    @PostMapping("/github/callback")
    @Operation(summary = "Link GitHub Account", description = "Receives OAuth 'code' from GitHub and links the GitHub account to the current user.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "GitHub linked successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid OAuth code"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or Invalid JWT")
    })
    public ResponseEntity<ApiResponse<String>> linkGithub(@RequestBody Map<String, String> payload) {
        User user = getCurrentUser();
        identityService.linkGithub(user.getId(), payload.get("code"));
        return ResponseEntity.ok(ApiResponse.success("Linked GitHub", "GitHub linked successfully"));
    }

    @PostMapping("/jira/callback")
    @Operation(summary = "Link Jira Account", description = "Receives OAuth 'code' from Jira and links the Jira account to the current user.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Jira linked successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid OAuth code"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or Invalid JWT")
    })
    public ResponseEntity<ApiResponse<String>> linkJira(@RequestBody Map<String, String> payload) {
        User user = getCurrentUser();
        identityService.linkJira(user.getId(), payload.get("code"));
        return ResponseEntity.ok(ApiResponse.success("Linked Jira", "Jira linked successfully"));
    }
}
