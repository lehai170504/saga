package com.saga.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableGithubRepoDTO {
    private String id;
    private String fullName;
    private String url;
    private boolean isPrivate;
}
