package com.saga.evaluation.service;

import com.saga.evaluation.dto.PeerReviewRequest;
import com.saga.evaluation.entity.PeerReview;
import com.saga.evaluation.repository.JpaPeerReviewRepository;
import com.saga.shared.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StudentEvaluationService {

    private final JpaPeerReviewRepository peerReviewRepository;

    public StudentEvaluationService(JpaPeerReviewRepository peerReviewRepository) {
        this.peerReviewRepository = peerReviewRepository;
    }

    @Transactional
    public PeerReview submitPeerReview(UUID reviewerId, PeerReviewRequest request) {
        if (reviewerId.equals(request.getRevieweeId())) {
            throw new BadRequestException("Reviewer and reviewee cannot be the same person");
        }

        PeerReview peerReview = PeerReview.builder()
                .sprintId(request.getSprintId())
                .reviewerId(reviewerId)
                .revieweeId(request.getRevieweeId())
                .processScore(request.getProcessScore())
                .technicalScore(request.getTechnicalScore())
                .teamworkScore(request.getTeamworkScore())
                .documentationScore(request.getDocumentationScore())
                .build();

        return peerReviewRepository.save(peerReview);
    }
}
