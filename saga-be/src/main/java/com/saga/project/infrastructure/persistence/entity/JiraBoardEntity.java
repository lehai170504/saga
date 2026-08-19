package com.saga.project.infrastructure.persistence.entity;
import com.saga.project.domain.IntegrationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "jira_boards")
@Getter
@Setter
public class JiraBoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "project_id", nullable = false)
    private UUID projectId;
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