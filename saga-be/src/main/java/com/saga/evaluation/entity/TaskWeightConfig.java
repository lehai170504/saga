package com.saga.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "task_weight_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskWeightConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    @Column(name = "team_id")
    private UUID teamId;

    @Column(name = "label_key", nullable = false)
    private String labelKey;

    @Column(name = "weight_percentage", nullable = false)
    private Double weightPercentage;
}
