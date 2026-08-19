package com.saga.project.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "tasks")
@Getter
@Setter
public class TaskEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID boardId;
    private String sprintId;
    private String issueKey;
    @ElementCollection
    private List<String> labels;
}