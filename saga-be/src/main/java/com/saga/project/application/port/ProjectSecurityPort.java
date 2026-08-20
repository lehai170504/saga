package com.saga.project.application.port;
import java.util.UUID;
public interface ProjectSecurityPort {
    boolean isLecturerOfTeam(UUID userId, UUID teamId);
    boolean isStudentInTeam(UUID userId, UUID teamId);
}