package com.saga.project.dto;

import lombok.Data;

@Data
public class JiraConfirmRequest {
    private String siteId;
    private String projectKey;
    private String boardName; // Optional
}
