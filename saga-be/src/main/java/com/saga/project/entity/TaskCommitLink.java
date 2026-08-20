package com.saga.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;
@Entity
@Table(name = "task_commit_links")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommitLink {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID taskId;
    private UUID commitId;
}