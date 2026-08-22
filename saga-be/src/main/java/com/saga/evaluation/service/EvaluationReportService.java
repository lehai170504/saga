package com.saga.evaluation.service;

import com.saga.academic.repository.JpaTeamMemberRepository;
import com.saga.academic.entity.TeamMember;
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
import com.saga.project.repository.JpaTaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EvaluationReportService {

    private final JpaTaskWeightConfigRepository weightConfigRepository;
    private final JpaPeerReviewRepository peerReviewRepository;
    private final JpaContributionOverrideRepository overrideRepository;
    private final JpaTaskRepository taskRepository;
    private final JpaTeamMemberRepository teamMemberRepository;

    public EvaluationReportService(JpaTaskWeightConfigRepository weightConfigRepository,
            JpaTaskRepository taskRepository,
            JpaPeerReviewRepository peerReviewRepository,
            JpaContributionOverrideRepository overrideRepository,
            JpaTeamMemberRepository teamMemberRepository) {
        this.weightConfigRepository = weightConfigRepository;
        this.peerReviewRepository = peerReviewRepository;
        this.overrideRepository = overrideRepository;
        this.taskRepository = taskRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public SprintReportDTO getSprintReport(UUID courseId, UUID teamId, String sprintId) {
        List<Task> sprintTasks = taskRepository.findBySprintIdAndStatus(sprintId, "DONE");
        List<TaskWeightConfig> weights = weightConfigRepository.findByTeamId(teamId);
        if (weights == null || weights.isEmpty()) {
            weights = weightConfigRepository.findByCourseId(courseId);
        }

        List<PeerReview> reviews = peerReviewRepository.findBySprintId(sprintId);

        List<UUID> teamMemberIds = teamMemberRepository.findByTeamId(teamId)
                .stream().map(TeamMember::getStudentId).collect(Collectors.toList());

        List<StudentContributionDTO> calculatedContributions = SlicingPieCalculator.calculate(sprintTasks, weights,
                reviews, teamMemberIds);

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
