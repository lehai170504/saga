package com.saga.evaluation.service;

import com.saga.evaluation.dto.TaskWeightBatchRequest;
import com.saga.evaluation.entity.TaskWeightConfig;
import com.saga.evaluation.repository.JpaTaskWeightConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EvaluationConfigService {

    private final JpaTaskWeightConfigRepository taskWeightConfigRepository;

    public EvaluationConfigService(JpaTaskWeightConfigRepository taskWeightConfigRepository) {
        this.taskWeightConfigRepository = taskWeightConfigRepository;
    }

    @Transactional
    public List<TaskWeightConfig> saveCourseWeights(UUID courseId, TaskWeightBatchRequest request) {
        validateRuleOf100(request.getItems());

        taskWeightConfigRepository.deleteByCourseId(courseId);

        List<TaskWeightConfig> configs = request.getItems().stream()
                .map(item -> TaskWeightConfig.builder()
                        .courseId(courseId)
                        .labelKey(item.getLabelKey())
                        .weightPercentage(item.getWeightPercentage())
                        .build())
                .collect(Collectors.toList());

        return taskWeightConfigRepository.saveAll(configs);
    }

    @Transactional
    public List<TaskWeightConfig> saveTeamWeights(UUID courseId, UUID teamId, TaskWeightBatchRequest request) {
        validateRuleOf100(request.getItems());

        taskWeightConfigRepository.deleteByTeamId(teamId);

        List<TaskWeightConfig> configs = request.getItems().stream()
                .map(item -> TaskWeightConfig.builder()
                        .courseId(courseId)
                        .teamId(teamId)
                        .labelKey(item.getLabelKey())
                        .weightPercentage(item.getWeightPercentage())
                        .build())
                .collect(Collectors.toList());

        return taskWeightConfigRepository.saveAll(configs);
    }

    private void validateRuleOf100(List<TaskWeightBatchRequest.TaskWeightItem> items) {
        double totalWeight = items.stream()
                .mapToDouble(TaskWeightBatchRequest.TaskWeightItem::getWeightPercentage)
                .sum();

        if (Math.abs(totalWeight - 100.0) > 0.0001) {
            throw new IllegalArgumentException("Tổng các trọng số phải chính xác bằng 100%");
        }
    }
}
