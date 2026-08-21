package com.saga.project.graph;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StudentNodeRepository extends Neo4jRepository<StudentNode, UUID> {
    @org.springframework.data.neo4j.repository.query.Query("MATCH (s:Student) WHERE NOT (s)-[:REVIEWED]-() RETURN count(s)")
    long countIsolatedStudents();
}
