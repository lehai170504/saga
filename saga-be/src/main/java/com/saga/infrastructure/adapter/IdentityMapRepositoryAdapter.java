package com.saga.infrastructure.adapter;
import com.saga.application.port.IdentityMapRepositoryPort;
import com.saga.domain.ExternalProvider;
import com.saga.domain.IdentityMap;
import com.saga.infrastructure.entity.IdentityMapEntity;
import com.saga.infrastructure.repository.SpringDataIdentityMapRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Component
public class IdentityMapRepositoryAdapter implements IdentityMapRepositoryPort {
    private final SpringDataIdentityMapRepository repository;
    public IdentityMapRepositoryAdapter(SpringDataIdentityMapRepository repository) { this.repository = repository; }
    @Override public IdentityMap save(IdentityMap identityMap) {
        IdentityMapEntity entity = new IdentityMapEntity();
        entity.setId(identityMap.getId()); entity.setInternalUserId(identityMap.getInternalUserId());
        entity.setExternalProvider(identityMap.getExternalProvider()); entity.setExternalId(identityMap.getExternalId());
        entity.setName(identityMap.getName()); entity.setEmail(identityMap.getEmail()); entity.setConnectedAt(identityMap.getConnectedAt());
        repository.save(entity); return identityMap;
    }
    @Override public void deleteByInternalUserIdAndExternalProvider(UUID userId, ExternalProvider provider) {
        repository.deleteByInternalUserIdAndExternalProvider(userId, provider);
    }
    @Override public List<IdentityMap> findByInternalUserId(UUID userId) {
        return repository.findByInternalUserId(userId).stream().map(entity -> 
            IdentityMap.builder().id(entity.getId()).internalUserId(entity.getInternalUserId())
                .externalProvider(entity.getExternalProvider()).externalId(entity.getExternalId())
                .name(entity.getName()).email(entity.getEmail()).connectedAt(entity.getConnectedAt()).build()
        ).collect(Collectors.toList());
    }
}