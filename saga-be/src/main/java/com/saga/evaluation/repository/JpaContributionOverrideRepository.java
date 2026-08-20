package com.saga.evaluation.repository;

import com.saga.evaluation.entity.ContributionOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaContributionOverrideRepository extends JpaRepository<ContributionOverride, UUID> {
    Optional<ContributionOverride> findBySprintIdAndStudentId(String sprintId, UUID studentId);
}
