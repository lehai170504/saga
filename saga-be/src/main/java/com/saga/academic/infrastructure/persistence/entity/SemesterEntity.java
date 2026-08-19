package com.saga.academic.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;
@Entity
@Table(name = "semesters")
@Getter
@Setter
public class SemesterEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}