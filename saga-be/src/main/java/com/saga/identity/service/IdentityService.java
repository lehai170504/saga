package com.saga.identity.service;

import com.saga.identity.repository.JpaIdentityMapRepository;
import com.saga.identity.entity.ExternalProvider;
import com.saga.identity.entity.IdentityMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class IdentityService {
    private final JpaIdentityMapRepository identityMapRepository;
    private final ExternalIdentityService externalIdentityPort;

    public IdentityService(JpaIdentityMapRepository identityMapRepository,
            ExternalIdentityService externalIdentityPort) {
        this.identityMapRepository = identityMapRepository;
        this.externalIdentityPort = externalIdentityPort;
    }

    @Transactional
    public void unlinkIdentity(UUID userId, ExternalProvider provider) {
        identityMapRepository.deleteByInternalUserIdAndExternalProvider(userId, provider);
    }

    public List<IdentityMap> getIdentities(UUID userId) {
        return identityMapRepository.findByInternalUserId(userId);
    }

    @Transactional
    public void linkGithub(UUID userId, String code) {
        com.saga.identity.service.ExternalUserProfile profile = externalIdentityPort.getGithubProfile(code);
        identityMapRepository.deleteByInternalUserIdAndExternalProvider(userId, ExternalProvider.GITHUB);
        IdentityMap map = IdentityMap.builder().id(UUID.randomUUID()).internalUserId(userId)
                .externalProvider(ExternalProvider.GITHUB).externalId(profile.getId())
                .name(profile.getName()).email(profile.getEmail()).connectedAt(java.time.LocalDateTime.now()).build();
        identityMapRepository.save(map);
    }

    @Transactional
    public void linkJira(UUID userId, String code) {
        com.saga.identity.service.ExternalUserProfile profile = externalIdentityPort.getJiraProfile(code);
        identityMapRepository.deleteByInternalUserIdAndExternalProvider(userId, ExternalProvider.JIRA);
        IdentityMap map = IdentityMap.builder().id(UUID.randomUUID()).internalUserId(userId)
                .externalProvider(ExternalProvider.JIRA).externalId(profile.getId())
                .name(profile.getName()).email(profile.getEmail()).connectedAt(java.time.LocalDateTime.now()).build();
        identityMapRepository.save(map);
    }
}

