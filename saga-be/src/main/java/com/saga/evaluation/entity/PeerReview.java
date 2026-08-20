package com.saga.evaluation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "peer_reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sprint_id", nullable = false)
    private String sprintId;

    @Column(name = "reviewer_id", nullable = false)
    private UUID reviewerId;

    @Column(name = "reviewee_id", nullable = false)
    private UUID revieweeId;

    @Column(name = "process_score", nullable = false)
    private Integer processScore;

    @Column(name = "technical_score", nullable = false)
    private Integer technicalScore;

    @Column(name = "teamwork_score", nullable = false)
    private Integer teamworkScore;

    @Column(name = "documentation_score", nullable = false)
    private Integer documentationScore;
}
