package com.saga.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "contribution_overrides")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContributionOverride {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sprint_id", nullable = false)
    private String sprintId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "lecturer_id", nullable = false)
    private UUID lecturerId;

    @Column(name = "overridden_percentage", nullable = false)
    private Double overriddenPercentage;

    @Column(name = "reason")
    private String reason;
}
