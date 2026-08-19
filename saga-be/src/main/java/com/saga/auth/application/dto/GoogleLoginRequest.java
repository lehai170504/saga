package com.saga.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for Google Login")
public class GoogleLoginRequest {
    @Schema(description = "Google ID Token", example = "eyJhbGciOiJSUzI1NiIs...")
    private String idToken;
}
