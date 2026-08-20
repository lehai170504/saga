package com.saga.evaluation.repository;

import com.saga.evaluation.entity.TaskWeightConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTaskWeightConfigRepository extends JpaRepository<TaskWeightConfig, UUID> {
    List<TaskWeightConfig> findByCourseId(UUID courseId);

    void deleteByCourseId(UUID courseId);

    List<TaskWeightConfig> findByTeamId(UUID teamId);

    void deleteByTeamId(UUID teamId);
}
