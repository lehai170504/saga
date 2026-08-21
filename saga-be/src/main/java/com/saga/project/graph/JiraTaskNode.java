package com.saga.project.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.UUID;

@Node("JiraTask")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraTaskNode {
    @Id
    private UUID id;
    
    private String issueKey;
    private Integer storyPoint;
    private String status;
}

