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

    private Double calculatedPercentage;

    private Double overriddenPercentage;
    private String overrideReason;

    private Double finalPercentage;
}
