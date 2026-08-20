package com.saga.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentContributionDTO {
    private UUID studentId;
    private Double basePoints;
    private Double retrospectiveCoefficient;
    private Double pieScore;

    // The calculated Slicing Pie percentage
    private Double calculatedPercentage;

    // Overridden values if applicable
    private Double overriddenPercentage;
    private String overrideReason;

    // The final applied percentage
    private Double finalPercentage;
}
