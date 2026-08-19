package com.saga.academic.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Entity
@Table(name = "classes")
@Getter
@Setter
public class ClassEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private String name;
}