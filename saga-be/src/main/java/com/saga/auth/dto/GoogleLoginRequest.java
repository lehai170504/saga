package com.saga.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for Google Login")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginRequest {
    @Schema(description = "Google ID Token", example = "eyJhbGciOiJSUzI1NiIs...")
    private String idToken;
}
