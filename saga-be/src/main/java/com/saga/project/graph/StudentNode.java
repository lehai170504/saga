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

@Node("Student")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentNode {
    @Id
    private UUID id;
    
    private String email;

    @Relationship(type = "AUTHORED", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<CommitNode> authoredCommits = new ArrayList<>();

    @Relationship(type = "ASSIGNED_TO", direction = Relationship.Direction.OUTGOING)
    @Builder.Default
    private List<JiraTaskNode> assignedTasks = new ArrayList<>();
}
