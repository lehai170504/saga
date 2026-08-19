package com.saga.academic.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Entity
@Table(name = "subjects")
@Getter
@Setter
public class SubjectEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private String code;
    private String name;
}