package com.saga.identity.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.saga.identity.entity.ExternalProvider;
import com.saga.identity.entity.IdentityMap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface JpaIdentityMapRepository
        extends JpaRepository<IdentityMap, UUID>, JpaSpecificationExecutor<IdentityMap> {
    void deleteByInternalUserIdAndExternalProvider(UUID userId, ExternalProvider provider);

    List<IdentityMap> findByInternalUserId(UUID userId);

    java.util.Optional<IdentityMap> findByExternalIdAndExternalProvider(String externalId, ExternalProvider provider);
}