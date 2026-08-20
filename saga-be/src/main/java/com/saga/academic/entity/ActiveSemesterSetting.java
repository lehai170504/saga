package com.saga.academic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;
@Entity
@Table(name = "active_semester_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSemesterSetting {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID semesterId;
}