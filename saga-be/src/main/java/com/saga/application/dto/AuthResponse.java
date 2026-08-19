package com.saga.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object for Authentication")
public class AuthResponse {
    @Schema(description = "Local JWT token")
    private String accessToken;

    @Schema(description = "User role")
    private String role;

    @Schema(description = "User Profile")
    private UserProfileDTO user;
}