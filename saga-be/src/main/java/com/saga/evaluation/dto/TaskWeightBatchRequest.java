package com.saga.evaluation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class TaskWeightBatchRequest {
    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotEmpty(message = "Weights list cannot be empty")
    @Valid
    private List<TaskWeightItem> items;

    @Data
    public static class TaskWeightItem {
        @NotEmpty(message = "Label key is required")
        private String labelKey;

        @NotNull(message = "Weight percentage is required")
        private Double weightPercentage;
    }
}
