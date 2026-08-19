package com.saga.project.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Entity
@Table(name = "commit_data")
@Getter
@Setter
public class CommitDataEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID repoId;
    private String hash;
    private String message;
    private String authorEmail;
    private String branchName;
}