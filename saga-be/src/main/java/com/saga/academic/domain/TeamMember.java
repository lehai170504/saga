package com.saga.academic.domain;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class TeamMember {
    private UUID id;
    private UUID teamId;
    private UUID studentId;
    private Boolean isLeader;
}