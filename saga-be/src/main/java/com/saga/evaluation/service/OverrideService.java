package com.saga.evaluation.service;

import com.saga.evaluation.dto.OverrideRequest;
import com.saga.evaluation.entity.ContributionOverride;
import com.saga.evaluation.repository.JpaContributionOverrideRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OverrideService {

    private final JpaContributionOverrideRepository overrideRepository;

    public OverrideService(JpaContributionOverrideRepository overrideRepository) {
        this.overrideRepository = overrideRepository;
    }

    @Transactional
    public ContributionOverride overrideStudentContribution(String sprintId, UUID studentId, UUID lecturerId,
            OverrideRequest request) {
        ContributionOverride override = overrideRepository.findBySprintIdAndStudentId(sprintId, studentId)
                .orElse(ContributionOverride.builder()
                        .sprintId(sprintId)
                        .studentId(studentId)
                        .build());

        override.setLecturerId(lecturerId);
        override.setOverriddenPercentage(request.getOverriddenPercentage());
        override.setReason(request.getReason());

        return overrideRepository.save(override);
    }
}
