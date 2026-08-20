package com.saga.academic.application.dto;
import com.saga.user.application.dto.UserResponseDTO;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TeamDetailDTO {
    private UUID id;
    private String name;
    private List<UserResponseDTO> members;
}
