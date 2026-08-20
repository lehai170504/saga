package com.saga.auth.application.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class LocalLoginRequest {
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    private String password;
}
