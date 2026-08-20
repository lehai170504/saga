package com.saga.evaluation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class PeerReviewRequest {
    @NotEmpty(message = "Sprint ID is required")
    private String sprintId;

    @NotNull(message = "Reviewee ID is required")
    private UUID revieweeId;

    @Min(value = 1, message = "Process score must be at least 1")
    @Max(value = 5, message = "Process score cannot exceed 5")
    @NotNull
    private Integer processScore;

    @Min(value = 1, message = "Technical score must be at least 1")
    @Max(value = 5, message = "Technical score cannot exceed 5")
    @NotNull
    private Integer technicalScore;

    @Min(value = 1, message = "Teamwork score must be at least 1")
    @Max(value = 5, message = "Teamwork score cannot exceed 5")
    @NotNull
    private Integer teamworkScore;

    @Min(value = 1, message = "Documentation score must be at least 1")
    @Max(value = 5, message = "Documentation score cannot exceed 5")
    @NotNull
    private Integer documentationScore;
}
