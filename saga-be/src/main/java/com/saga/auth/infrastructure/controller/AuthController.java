package com.saga.auth.infrastructure.controller;

import com.saga.auth.application.dto.AuthResponse;
import com.saga.auth.application.dto.GoogleLoginRequest;
import com.saga.auth.application.dto.UserProfileDTO;
import com.saga.auth.application.port.TokenBlacklistPort;
import com.saga.user.application.port.UserRepositoryPort;
import com.saga.auth.application.usecase.LoginUseCase;
import com.saga.user.domain.User;
import com.saga.shared.exception.UnauthorizedException;
import com.saga.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and authorization")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final TokenBlacklistPort tokenBlacklistPort;
    private final UserRepositoryPort userRepositoryPort;

    public AuthController(LoginUseCase loginUseCase, TokenBlacklistPort tokenBlacklistPort, UserRepositoryPort userRepositoryPort) {
        this.loginUseCase = loginUseCase;
        this.tokenBlacklistPort = tokenBlacklistPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @PostMapping("/login")
    @Operation(summary = "Login with Google", description = "Receive Google Access Token and return Local JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody GoogleLoginRequest request) {
        AuthResponse response = loginUseCase.loginWithGoogle(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns User Profile based on JWT")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getMe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) auth.getPrincipal();
        
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        
        UserProfileDTO profile = UserProfileDTO.builder()
                .email(user.getEmail())
                .name(user.getName())
                .picture(user.getPicture())
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(profile, "Get profile successfully"));
    }

    @GetMapping("/csrf")
    @Operation(summary = "Get CSRF Token", description = "Returns CSRF Token")
    public ResponseEntity<ApiResponse<CsrfToken>> getCsrfToken(HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return ResponseEntity.ok(ApiResponse.success(csrfToken, "CSRF Token retrieved"));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates the current JWT by putting it in the blacklist")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistPort.blacklistToken(token);
        }
        return ResponseEntity.ok(ApiResponse.success("Logged out", "Logout successfully"));
    }
}
