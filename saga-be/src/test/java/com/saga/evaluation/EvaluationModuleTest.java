package com.saga.evaluation;

import com.saga.evaluation.dto.SprintReportDTO;
import com.saga.evaluation.dto.StudentContributionDTO;
import com.saga.evaluation.dto.TaskWeightBatchRequest;
import com.saga.evaluation.entity.ContributionOverride;
import com.saga.evaluation.entity.TaskWeightConfig;
import com.saga.evaluation.repository.JpaContributionOverrideRepository;
import com.saga.evaluation.repository.JpaPeerReviewRepository;
import com.saga.evaluation.repository.JpaTaskWeightConfigRepository;
import com.saga.evaluation.service.EvaluationConfigService;
import com.saga.evaluation.service.EvaluationReportService;
import com.saga.project.entity.Task;
import com.saga.project.repository.JpaTaskRepository;
import com.saga.academic.repository.JpaTeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EvaluationModuleTest {

        @Mock
        private JpaTaskWeightConfigRepository taskWeightConfigRepository;

        @Mock
        private JpaPeerReviewRepository peerReviewRepository;

        @Mock
        private JpaContributionOverrideRepository overrideRepository;

        @Mock
        private JpaTaskRepository taskRepository;

        @Mock
        private JpaTeamMemberRepository teamMemberRepository;

        @InjectMocks
        private EvaluationConfigService evaluationConfigService;

        @InjectMocks
        private EvaluationReportService evaluationReportService;

        @Test
        void testTheRuleOf100_TwoLabelsSum100_DoesNotThrow() {
                TaskWeightBatchRequest request = new TaskWeightBatchRequest();

                TaskWeightBatchRequest.TaskWeightItem item1 = new TaskWeightBatchRequest.TaskWeightItem();
                item1.setLabelKey("saga:doc");
                item1.setWeightPercentage(70.0);

                TaskWeightBatchRequest.TaskWeightItem item2 = new TaskWeightBatchRequest.TaskWeightItem();
                item2.setLabelKey("saga:research");
                item2.setWeightPercentage(30.0);

                request.setItems(Arrays.asList(item1, item2));

                assertDoesNotThrow(() -> {
                        evaluationConfigService.saveCourseWeights(UUID.randomUUID(), request);
                });
        }

        @Test
        void testEvaluationReportService_AppliesOverride() {
                UUID studentA = UUID.randomUUID();
                String sprintId = "Sprint-1";

                Task taskA = Task.builder()
                                .assigneeId(studentA)
                                .storyPoint(10)
                                .labels(List.of("saga:doc"))
                                .build();

                TaskWeightConfig weight = TaskWeightConfig.builder()
                                .labelKey("saga:doc")
                                .weightPercentage(100.0)
                                .build();

                ContributionOverride override = ContributionOverride.builder()
                                .studentId(studentA)
                                .sprintId(sprintId)
                                .overriddenPercentage(50.0)
                                .reason("Penalty")
                                .build();

                when(taskWeightConfigRepository.findByTeamId(any())).thenReturn(Collections.emptyList());
                when(taskWeightConfigRepository.findByCourseId(any())).thenReturn(List.of(weight));
                when(peerReviewRepository.findBySprintId(any())).thenReturn(Collections.emptyList());
                when(overrideRepository.findBySprintIdAndStudentId(sprintId, studentA))
                                .thenReturn(Optional.of(override));

                when(taskRepository.findBySprintIdAndStatus(sprintId, "DONE")).thenReturn(List.of(taskA));
                when(teamMemberRepository.findByTeamId(any())).thenReturn(Collections.emptyList());

                SprintReportDTO report = evaluationReportService.getSprintReport(UUID.randomUUID(), UUID.randomUUID(),
                                sprintId);

                assertThat(report.getContributions()).hasSize(1);
                StudentContributionDTO dtoA = report.getContributions().get(0);

                assertThat(dtoA.getCalculatedPercentage()).isEqualTo(100.0);
                assertThat(dtoA.getFinalPercentage()).isEqualTo(50.0);
                assertThat(dtoA.getOverriddenPercentage()).isEqualTo(50.0);
                assertThat(dtoA.getOverrideReason()).isEqualTo("Penalty");
        }
}
