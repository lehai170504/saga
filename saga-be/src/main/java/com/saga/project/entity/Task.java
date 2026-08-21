package com.saga.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID boardId;
    private String sprintId;
    private String issueKey;
    private Integer storyPoint;
    private UUID assigneeId;
    @ElementCollection
    private List<String> labels;
    
    @Column(name = "summary")
    private String summary;
    
    @Column(name = "status")
    private String status;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_attachments", joinColumns = @JoinColumn(name = "task_id"))
    private List<TaskAttachment> attachments;
}
