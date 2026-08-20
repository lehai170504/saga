package com.saga.evaluation.calculator;

import com.saga.evaluation.dto.StudentContributionDTO;
import com.saga.evaluation.entity.PeerReview;
import com.saga.evaluation.entity.TaskWeightConfig;
import com.saga.project.entity.Task;

import java.util.*;
import java.util.stream.Collectors;

public class SlicingPieCalculator {

    public static List<StudentContributionDTO> calculate(List<Task> tasks, List<TaskWeightConfig> weights,
            List<PeerReview> peerReviews) {
        Map<String, Double> weightMap = weights.stream()
                .collect(Collectors.toMap(TaskWeightConfig::getLabelKey, TaskWeightConfig::getWeightPercentage));

        Map<UUID, List<Task>> tasksByStudent = tasks.stream()
                .filter(t -> t.getAssigneeId() != null)
                .collect(Collectors.groupingBy(Task::getAssigneeId));

        Map<UUID, List<PeerReview>> reviewsByStudent = peerReviews.stream()
                .collect(Collectors.groupingBy(PeerReview::getRevieweeId));

        List<StudentContributionDTO> results = new ArrayList<>();
        double totalPieScore = 0.0;

        Set<UUID> allStudentIds = new HashSet<>();
        allStudentIds.addAll(tasksByStudent.keySet());
        allStudentIds.addAll(reviewsByStudent.keySet());

        for (UUID studentId : allStudentIds) {
            double basePoints = 0.0;
            List<Task> studentTasks = tasksByStudent.getOrDefault(studentId, Collections.emptyList());

            for (Task task : studentTasks) {
                if (task.getStoryPoint() == null || task.getStoryPoint() <= 0)
                    continue;

                double taskWeight = 0.0;
                if (task.getLabels() != null) {
                    for (String label : task.getLabels()) {
                        if (weightMap.containsKey(label)) {
                            taskWeight += weightMap.get(label) / 100.0;
                        }
                    }
                }
                basePoints += task.getStoryPoint() * taskWeight;
            }

            double r = 1.0;
            List<PeerReview> studentReviews = reviewsByStudent.getOrDefault(studentId, Collections.emptyList());
            if (!studentReviews.isEmpty()) {
                double sumScores = 0.0;
                for (PeerReview pr : studentReviews) {
                    sumScores += (pr.getProcessScore() + pr.getTechnicalScore() + pr.getTeamworkScore()
                            + pr.getDocumentationScore());
                }
                r = sumScores / (studentReviews.size() * 20.0);
            }

            double pieScore = basePoints * r;
            totalPieScore += pieScore;

            results.add(StudentContributionDTO.builder()
                    .studentId(studentId)
                    .basePoints(basePoints)
                    .retrospectiveCoefficient(r)
                    .pieScore(pieScore)
                    .build());
        }

        for (StudentContributionDTO dto : results) {
            if (totalPieScore > 0) {
                dto.setCalculatedPercentage((dto.getPieScore() / totalPieScore) * 100.0);
            } else {
                dto.setCalculatedPercentage(0.0);
            }
        }

        return results;
    }
}
