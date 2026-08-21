package com.saga.project.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphDataDTO {
    private List<NodeDTO> nodes;
    private List<EdgeDTO> edges;
}
