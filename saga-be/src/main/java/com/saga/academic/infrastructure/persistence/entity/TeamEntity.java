package com.saga.academic.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Entity
@Table(name = "teams")
@Getter
@Setter
public class TeamEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID courseId;
    private String name;
}