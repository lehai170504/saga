package com.saga.identity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "identity_map")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityMap {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "internal_user_id", nullable = false)
    private UUID internalUserId;
    @Enumerated(EnumType.STRING)
    @Column(name = "external_provider", nullable = false)
    private ExternalProvider externalProvider;
    @Column(name = "external_id", nullable = false)
    private String externalId;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "connected_at")
    private java.time.LocalDateTime connectedAt;
}