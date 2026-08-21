package com.saga.project.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Node("Commit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitNode {
    @Id
    private UUID id;

    private String hash;

    @Relationship(type = "IMPLEMENTS", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<JiraTaskNode> implementsTasks = new ArrayList<>();
}
