package com.saga.application.port;
import com.saga.domain.ExternalProvider;
import com.saga.domain.IdentityMap;
import java.util.List;
import java.util.UUID;
public interface IdentityMapRepositoryPort {
    IdentityMap save(IdentityMap identityMap);
    void deleteByInternalUserIdAndExternalProvider(UUID userId, ExternalProvider provider);
    List<IdentityMap> findByInternalUserId(UUID userId);
}