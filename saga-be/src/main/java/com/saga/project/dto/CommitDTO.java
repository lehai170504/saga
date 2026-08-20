package com.saga.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitDTO {
    private UUID id;
    private String hash;
    private String message;
    private String authorEmail;
    private String branchName;
    private LocalDateTime createdAt;
}