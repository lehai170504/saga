package com.saga.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "jira_boards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID teamId;
    @Column(name = "board_id", nullable = false)
    private String boardId;
    @Column(name = "board_name")
    private String boardName;
    @Column(name = "project_key")
    private String projectKey;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IntegrationStatus status;
    @Column(name = "linked_at")
    private LocalDateTime linkedAt;
}