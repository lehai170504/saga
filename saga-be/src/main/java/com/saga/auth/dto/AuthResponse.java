package com.saga.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object for Authentication")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    @Schema(description = "Local JWT token")
    private String accessToken;

    @Schema(description = "User role")
    private String role;

    @Schema(description = "User Profile")
    private UserProfileDTO user;
}