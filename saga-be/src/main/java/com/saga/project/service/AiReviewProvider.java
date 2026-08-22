package com.saga.project.service;

import com.saga.project.dto.AiReviewResult;

public interface AiReviewProvider {
    AiReviewResult analyzeCommit(String taskDescription, String gitDiff);

    String getProviderName();
}
