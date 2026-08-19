package com.saga.academic.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Entity
@Table(name = "courses")
@Getter
@Setter
public class CourseEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID semesterId;
    private UUID subjectId;
    private UUID classId;
    private UUID instructorId;
}