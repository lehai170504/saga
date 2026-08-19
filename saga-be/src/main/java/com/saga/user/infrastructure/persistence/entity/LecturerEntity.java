package com.saga.user.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "lecturers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LecturerEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;
}
