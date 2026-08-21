package com.saga.project.graph;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JiraTaskNodeRepository extends Neo4jRepository<JiraTaskNode, UUID> {
    java.util.Optional<JiraTaskNode> findByIssueKey(String issueKey);
}

