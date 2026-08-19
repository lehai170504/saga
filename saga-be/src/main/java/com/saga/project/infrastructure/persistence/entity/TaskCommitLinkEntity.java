package com.saga.project.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Entity
@Table(name = "task_commit_links")
@Getter
@Setter
public class TaskCommitLinkEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID taskId;
    private UUID commitId;
}