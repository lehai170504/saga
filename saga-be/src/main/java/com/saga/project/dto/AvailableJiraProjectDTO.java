package com.saga.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableJiraProjectDTO {
    private String id;
    private String key;
    private String name;
    private String style; // classic or next-gen
}
