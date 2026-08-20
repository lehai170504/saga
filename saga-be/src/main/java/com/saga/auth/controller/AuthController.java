package com.saga.auth.controller;

import com.saga.auth.dto.AuthResponse;
import com.saga.auth.dto.GoogleLoginRequest;
import com.saga.auth.dto.LocalLoginRequest;
import jakarta.validation.Valid;
import com.saga.auth.dto.UserProfileDTO;
import com.saga.auth.service.TokenBlacklistService;
import com.saga.auth.service.RefreshTokenService;
import com.saga.user.repository.JpaUserRepository;
import com.saga.auth.service.LoginUseCase;
import com.saga.auth.service.JwtProviderService;
import com.saga.user.entity.User;
import com.saga.shared.exception.UnauthorizedException;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "1. Auth APIs", description = "Endpoints for User Authentication & Authorization (Login, Logout, Token Refresh)")
public class AuthController {

        private final RefreshTokenService refreshTokenService;
        private final JwtProviderService jwtProviderPort;
        private final LoginUseCase loginUseCase;
        private final TokenBlacklistService tokenBlacklistPort;
        private final JpaUserRepository userRepository;

        public AuthController(LoginUseCase loginUseCase,
                        JwtProviderService jwtProviderPort,
                        TokenBlacklistService tokenBlacklistPort,
                        JpaUserRepository userRepository,
                        RefreshTokenService refreshTokenService) {
                this.loginUseCase = loginUseCase;
                this.jwtProviderPort = jwtProviderPort;
                this.tokenBlacklistPort = tokenBlacklistPort;
                this.userRepository = userRepository;
                this.refreshTokenService = refreshTokenService;
        }

        @PostMapping("/login-local")
        @Operation(summary = "Local Login (Admin & Lecturer)", description = "Login with Email and Password for seeded accounts.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid email or password")
        })
        public ResponseEntity<ApiResponse<AuthResponse>> loginLocal(@Valid @RequestBody LocalLoginRequest request) {
                AuthResponse response = loginUseCase.loginLocal(request);
                return ResponseEntity.ok(ApiResponse.success(response, "Login successfully"));
        }

        @PostMapping("/login")
        public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody GoogleLoginRequest request) {
                AuthResponse response = loginUseCase.loginWithGoogle(request);
                return ResponseEntity.ok(ApiResponse.success(response, "Login successfully"));
        }

        @PostMapping("/refresh")
        @Operation(summary = "Refresh JWT Token", description = "Generate a new JWT using a valid Refresh Token.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "New Access Token Generated"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or Expired refresh token")
        })
        public ResponseEntity<ApiResponse<String>> refreshToken(@RequestParam String token) {
                return refreshTokenService.findByToken(token)
                                .map(refreshToken -> ResponseEntity
                                                .ok(ApiResponse.success("New Access Token Generated",
                                                                "Refresh successfully")))
                                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        }

        @GetMapping("/me")
        @Operation(summary = "Get current user profile", description = "Returns User Profile based on the Bearer JWT in the Authorization header.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Get profile successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or Invalid JWT")
        })
        public ResponseEntity<ApiResponse<UserProfileDTO>> getMe() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                String email = (String) auth.getPrincipal();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UnauthorizedException("User not found"));

                UserProfileDTO profile = UserProfileDTO.builder()
                                .email(user.getEmail())
                                .name(user.getName())
                                .picture(user.getPicture())
                                .build();

                return ResponseEntity.ok(ApiResponse.success(profile, "Get profile successfully"));
        }

        @GetMapping("/csrf")
        @Operation(summary = "Get CSRF Token", description = "Returns CSRF Token for state-mutating requests (if CSRF is enabled).")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "CSRF Token retrieved")
        })
        public ResponseEntity<ApiResponse<CsrfToken>> getCsrfToken(HttpServletRequest request) {
                CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                return ResponseEntity.ok(ApiResponse.success(csrfToken, "CSRF Token retrieved"));
        }

        @PostMapping("/logout")
        @Operation(summary = "Logout", description = "Invalidates the current JWT by adding it to the blacklist.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout successfully"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized - Missing or Invalid JWT")
        })
        public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        tokenBlacklistPort.blacklistToken(token);
                }
                return ResponseEntity.ok(ApiResponse.success("Logged out", "Logout successfully"));
        }
}

