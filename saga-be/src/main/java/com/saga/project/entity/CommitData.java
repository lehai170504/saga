package com.saga.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;
@Entity
@Table(name = "commit_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommitData {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID repoId;
    private String hash;
    private String message;
    private String authorEmail;
    private String branchName;
}