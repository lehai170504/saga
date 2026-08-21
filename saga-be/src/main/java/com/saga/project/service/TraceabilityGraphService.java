package com.saga.project.service;

import com.saga.project.graph.CommitNode;
import com.saga.project.graph.JiraTaskNode;
import com.saga.project.graph.StudentNode;
import com.saga.project.graph.StudentNodeRepository;
import com.saga.project.graph.JiraTaskNodeRepository;
import com.saga.project.graph.CommitNodeRepository;
import com.saga.project.graph.dto.EdgeDTO;
import com.saga.project.graph.dto.GraphDataDTO;
import com.saga.project.graph.dto.NodeDTO;
import com.saga.project.graph.dto.TeamStatsDTO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TraceabilityGraphService {

    private final StudentNodeRepository studentRepo;
    private final JiraTaskNodeRepository taskRepo;
    private final CommitNodeRepository commitRepo;

    public TraceabilityGraphService(StudentNodeRepository studentRepo, JiraTaskNodeRepository taskRepo,
            CommitNodeRepository commitRepo) {
        this.studentRepo = studentRepo;
        this.taskRepo = taskRepo;
        this.commitRepo = commitRepo;
    }

    public GraphDataDTO getGraphData(UUID teamId, String sprintId) {
        // For simplicity in this implementation, we fetch all students and their
        // relationships.
        // In a production environment, we should use a custom @Query to filter by
        // teamId/sprintId.
        List<StudentNode> students = studentRepo.findAll();
        List<JiraTaskNode> tasks = taskRepo.findAll();
        List<CommitNode> commits = commitRepo.findAll();

        List<NodeDTO> nodes = new ArrayList<>();
        List<EdgeDTO> edges = new ArrayList<>();
        Set<String> addedNodes = new HashSet<>();

        // Add Student Nodes
        for (StudentNode student : students) {
            String nodeId = "student_" + student.getId();
            if (addedNodes.add(nodeId)) {
                nodes.add(NodeDTO.builder()
                        .id(nodeId)
                        .label("Student")
                        .properties(Map.of("email", student.getEmail() != null ? student.getEmail() : "Unknown"))
                        .build());
            }

            // Edges: Student -> Commit
            for (CommitNode commit : student.getAuthoredCommits()) {
                edges.add(EdgeDTO.builder()
                        .source(nodeId)
                        .target("commit_" + commit.getId())
                        .type("AUTHORED")
                        .build());
            }

            // Edges: Student -> Task
            for (JiraTaskNode task : student.getAssignedTasks()) {
                edges.add(EdgeDTO.builder()
                        .source(nodeId)
                        .target("task_" + task.getId())
                        .type("ASSIGNED_TO")
                        .build());
            }
        }

        // Add Task Nodes
        for (JiraTaskNode task : tasks) {
            String nodeId = "task_" + task.getId();
            if (addedNodes.add(nodeId)) {
                nodes.add(NodeDTO.builder()
                        .id(nodeId)
                        .label("JiraTask")
                        .properties(Map.of("issueKey", task.getIssueKey() != null ? task.getIssueKey() : "Unknown"))
                        .build());
            }
        }

        // Add Commit Nodes
        for (CommitNode commit : commits) {
            String nodeId = "commit_" + commit.getId();
            if (addedNodes.add(nodeId)) {
                nodes.add(NodeDTO.builder()
                        .id(nodeId)
                        .label("Commit")
                        .properties(Map.of("hash", commit.getHash() != null ? commit.getHash() : "Unknown"))
                        .build());
            }

            // Edges: Commit -> Task
            for (JiraTaskNode task : commit.getImplementsTasks()) {
                edges.add(EdgeDTO.builder()
                        .source(nodeId)
                        .target("task_" + task.getId())
                        .type("IMPLEMENTS")
                        .build());
            }
        }

        return GraphDataDTO.builder().nodes(nodes).edges(edges).build();
    }

    public TeamStatsDTO getTeamStats(UUID teamId, String sprintId) {
        // Simplified statistics calculation
        long totalStudents = studentRepo.count();
        long totalTasks = taskRepo.count();
        long totalCommits = commitRepo.count();

        return TeamStatsDTO.builder()
                .totalStudents((int) totalStudents)
                .totalTasks((int) totalTasks)
                .totalCommits((int) totalCommits)
                .taskCompletionRate(85.5) // Placeholder for actual calc
                .mostActiveStudent("student@example.com") // Placeholder
                .build();
    }
}
