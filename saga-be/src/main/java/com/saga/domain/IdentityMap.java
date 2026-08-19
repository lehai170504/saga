package com.saga.domain;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class IdentityMap {
    private UUID id;
    private UUID internalUserId;
    private ExternalProvider externalProvider;
    private String externalId;
    private String name;
    private String email;
    private java.time.LocalDateTime connectedAt;
}