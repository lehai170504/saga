package com.saga.project.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdgeDTO {
    private String source;
    private String target;
    private String type; // AUTHORED, ASSIGNED_TO, IMPLEMENTS
}
