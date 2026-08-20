package com.saga.evaluation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SprintReportDTO {
    private String sprintId;
    private List<StudentContributionDTO> contributions;
}
