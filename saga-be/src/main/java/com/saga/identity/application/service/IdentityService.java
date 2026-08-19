package com.saga.identity.application.service;
import com.saga.identity.application.port.ExternalIdentityPort;
import com.saga.identity.application.port.IdentityMapRepositoryPort;
import com.saga.identity.domain.ExternalProvider;
import com.saga.identity.domain.IdentityMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
@Service
public class IdentityService {
    private final IdentityMapRepositoryPort identityMapRepositoryPort;
    private final ExternalIdentityPort externalIdentityPort;
    public IdentityService(IdentityMapRepositoryPort identityMapRepositoryPort, ExternalIdentityPort externalIdentityPort) {
        this.identityMapRepositoryPort = identityMapRepositoryPort;
        this.externalIdentityPort = externalIdentityPort;
    }
    @Transactional
    public void unlinkIdentity(UUID userId, ExternalProvider provider) {
        identityMapRepositoryPort.deleteByInternalUserIdAndExternalProvider(userId, provider);
    }
    public List<IdentityMap> getIdentities(UUID userId) {
        return identityMapRepositoryPort.findByInternalUserId(userId);
    }
    @Transactional
    public void linkGithub(UUID userId, String code) {
        com.saga.identity.application.port.ExternalUserProfile profile = externalIdentityPort.getGithubProfile(code);
        identityMapRepositoryPort.deleteByInternalUserIdAndExternalProvider(userId, ExternalProvider.GITHUB);
        IdentityMap map = IdentityMap.builder().id(UUID.randomUUID()).internalUserId(userId)
                .externalProvider(ExternalProvider.GITHUB).externalId(profile.getId())
                .name(profile.getName()).email(profile.getEmail()).connectedAt(java.time.LocalDateTime.now()).build();
        identityMapRepositoryPort.save(map);
    }
    @Transactional
    public void linkJira(UUID userId, String code) {
        com.saga.identity.application.port.ExternalUserProfile profile = externalIdentityPort.getJiraProfile(code);
        identityMapRepositoryPort.deleteByInternalUserIdAndExternalProvider(userId, ExternalProvider.JIRA);
        IdentityMap map = IdentityMap.builder().id(UUID.randomUUID()).internalUserId(userId)
                .externalProvider(ExternalProvider.JIRA).externalId(profile.getId())
                .name(profile.getName()).email(profile.getEmail()).connectedAt(java.time.LocalDateTime.now()).build();
        identityMapRepositoryPort.save(map);
    }
}
