package com.saga.project.graph;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommitNodeRepository extends Neo4jRepository<CommitNode, UUID> {
}
