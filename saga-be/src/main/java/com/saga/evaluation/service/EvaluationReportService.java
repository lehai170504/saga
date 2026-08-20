package com.saga.evaluation.service;

import com.saga.evaluation.calculator.SlicingPieCalculator;
import com.saga.evaluation.dto.SprintReportDTO;
import com.saga.evaluation.dto.StudentContributionDTO;
import com.saga.evaluation.entity.ContributionOverride;
import com.saga.evaluation.entity.PeerReview;
import com.saga.evaluation.entity.TaskWeightConfig;
import com.saga.evaluation.repository.JpaContributionOverrideRepository;
import com.saga.evaluation.repository.JpaPeerReviewRepository;
import com.saga.evaluation.repository.JpaTaskWeightConfigRepository;
import com.saga.project.entity.Task;
// Assume we have JpaTaskRepository or something similar. 
// For now, I'll assume we can fetch tasks somehow. I'll mock the dependency or create it.
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EvaluationReportService {

    private final JpaTaskWeightConfigRepository weightConfigRepository;
    private final JpaPeerReviewRepository peerReviewRepository;
    private final JpaContributionOverrideRepository overrideRepository;
    // Private task repo dependency to be added if needed...

    public EvaluationReportService(JpaTaskWeightConfigRepository weightConfigRepository,
            JpaPeerReviewRepository peerReviewRepository,
            JpaContributionOverrideRepository overrideRepository) {
        this.weightConfigRepository = weightConfigRepository;
        this.peerReviewRepository = peerReviewRepository;
        this.overrideRepository = overrideRepository;
    }

    public SprintReportDTO getSprintReport(UUID courseId, UUID teamId, String sprintId, List<Task> sprintTasks) {
        // Fallback logic for weights
        List<TaskWeightConfig> weights = weightConfigRepository.findByTeamId(teamId);
        if (weights == null || weights.isEmpty()) {
            weights = weightConfigRepository.findByCourseId(courseId);
        }

        List<PeerReview> reviews = peerReviewRepository.findBySprintId(sprintId);

        List<StudentContributionDTO> calculatedContributions = SlicingPieCalculator.calculate(sprintTasks, weights,
                reviews);

        for (StudentContributionDTO dto : calculatedContributions) {
            Optional<ContributionOverride> overrideOpt = overrideRepository.findBySprintIdAndStudentId(sprintId,
                    dto.getStudentId());
            if (overrideOpt.isPresent()) {
                ContributionOverride override = overrideOpt.get();
                dto.setOverriddenPercentage(override.getOverriddenPercentage());
                dto.setOverrideReason(override.getReason());
                dto.setFinalPercentage(override.getOverriddenPercentage());
            } else {
                dto.setFinalPercentage(dto.getCalculatedPercentage());
            }
        }

        return SprintReportDTO.builder()
                .sprintId(sprintId)
                .contributions(calculatedContributions)
                .build();
    }
}
