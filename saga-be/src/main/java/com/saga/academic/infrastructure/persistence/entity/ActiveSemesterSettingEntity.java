package com.saga.academic.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Entity
@Table(name = "active_semester_settings")
@Getter
@Setter
public class ActiveSemesterSettingEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID semesterId;
}