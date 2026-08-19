package com.saga.infrastructure.persistence.entity;

import com.saga.domain.Role;
import com.saga.domain.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    private String name;
    private String picture;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;
}
