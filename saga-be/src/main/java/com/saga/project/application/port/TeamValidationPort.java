package com.saga.project.application.port;
import java.util.UUID;
public interface TeamValidationPort {
    boolean isLeader(UUID userId, UUID teamId);
}