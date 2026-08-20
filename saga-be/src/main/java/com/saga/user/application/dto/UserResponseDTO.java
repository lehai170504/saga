package com.saga.user.application.dto;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class UserResponseDTO {
    private UUID id;
    private String email;
    private String name;
    private String picture;
    private String role;
    private String status;
}
