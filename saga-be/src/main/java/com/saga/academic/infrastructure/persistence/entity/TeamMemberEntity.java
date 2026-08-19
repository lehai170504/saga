package com.saga.academic.infrastructure.persistence.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Entity
@Table(name = "team_members")
@Getter
@Setter
public class TeamMemberEntity {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    private UUID teamId;
    private UUID studentId;
    private Boolean isLeader;
}