package com.saga.identity.infrastructure.persistence.repository;
import com.saga.identity.domain.ExternalProvider;
import com.saga.identity.infrastructure.persistence.entity.IdentityMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface SpringDataIdentityMapRepository extends JpaRepository<IdentityMapEntity, UUID> {
    void deleteByInternalUserIdAndExternalProvider(UUID userId, ExternalProvider provider);
    List<IdentityMapEntity> findByInternalUserId(UUID userId);
}