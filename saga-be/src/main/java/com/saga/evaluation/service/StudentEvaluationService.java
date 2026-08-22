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
    @com.saga.shared.annotation.LogAction(actionType = "SUBMIT_PEER_REVIEW")
    public PeerReview submitPeerReview(UUID reviewerId, PeerReviewRequest request) {
        if (reviewerId.equals(request.getRevieweeId())) {
            throw new BadRequestException("Reviewer and reviewee cannot be the same person");
        }

        java.util.Optional<PeerReview> existingOpt = peerReviewRepository.findBySprintIdAndReviewerIdAndRevieweeId(
                request.getSprintId(), reviewerId, request.getRevieweeId());

        PeerReview peerReview = existingOpt.orElseGet(PeerReview::new);
        peerReview.setSprintId(request.getSprintId());
        peerReview.setReviewerId(reviewerId);
        peerReview.setRevieweeId(request.getRevieweeId());
        peerReview.setProcessScore(request.getProcessScore());
        peerReview.setTechnicalScore(request.getTechnicalScore());
        peerReview.setTeamworkScore(request.getTeamworkScore());
        peerReview.setDocumentationScore(request.getDocumentationScore());

        return peerReviewRepository.save(peerReview);
    }
}
