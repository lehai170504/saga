package com.saga.academic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class UpdateTeamLeaderRequest {
    @NotNull(message = "New leader ID cannot be null")
    private UUID newLeaderStudentId;
}
