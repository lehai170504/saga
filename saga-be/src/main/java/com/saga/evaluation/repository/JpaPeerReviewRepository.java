package com.saga.evaluation.repository;

import com.saga.evaluation.entity.PeerReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaPeerReviewRepository extends JpaRepository<PeerReview, UUID> {
    List<PeerReview> findBySprintId(String sprintId);

    List<PeerReview> findByRevieweeId(UUID revieweeId);
}
