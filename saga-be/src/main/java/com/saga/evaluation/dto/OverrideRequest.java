package com.saga.evaluation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OverrideRequest {
    @NotNull(message = "Overridden percentage is required")
    @Min(value = 0, message = "Percentage cannot be less than 0")
    @Max(value = 100, message = "Percentage cannot exceed 100")
    private Double overriddenPercentage;

    private String reason;
}
