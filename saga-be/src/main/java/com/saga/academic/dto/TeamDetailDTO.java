package com.saga.academic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.saga.user.dto.UserResponseDTO;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDetailDTO {
    private UUID id;
    private String name;
    private List<UserResponseDTO> members;
}
