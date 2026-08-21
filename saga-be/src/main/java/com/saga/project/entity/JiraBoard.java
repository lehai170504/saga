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
    @Column(name = "site_id")
    private String siteId;
    @Column(name = "site_url")
    private String siteUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private IntegrationStatus status;
    @Column(name = "linked_at")
    private LocalDateTime linkedAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status")
    private SyncStatus syncStatus;
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
    @Column(name = "last_sync_message")
    private String lastSyncMessage;
    
    @Column(length = 2048)
    private String accessToken;
    
    @Column(length = 2048)
    private String refreshToken;
}



