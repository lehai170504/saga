package com.saga.identity.application.port;
import com.saga.identity.domain.ExternalProvider;
import com.saga.identity.domain.IdentityMap;
import java.util.List;
import java.util.UUID;
public interface IdentityMapRepositoryPort {
    IdentityMap save(IdentityMap identityMap);
    void deleteByInternalUserIdAndExternalProvider(UUID userId, ExternalProvider provider);
    List<IdentityMap> findByInternalUserId(UUID userId);
}