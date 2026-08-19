package com.saga.application.service;

import com.saga.application.port.ExternalIdentityPort;
import com.saga.application.port.ExternalUserProfile;
import com.saga.application.port.IdentityMapRepositoryPort;
import com.saga.domain.ExternalProvider;
import com.saga.domain.IdentityMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {
    @Mock
    private IdentityMapRepositoryPort identityMapRepositoryPort;
    @Mock
    private ExternalIdentityPort externalIdentityPort;
    @InjectMocks
    private IdentityService identityService;

    @Test
    void testLinkGithub_Success() {
        UUID userId = UUID.randomUUID();
        ExternalUserProfile profile = ExternalUserProfile.builder()
                .id("123456")
                .name("Test User")
                .email("test@github.com")
                .build();
        when(externalIdentityPort.getGithubProfile("dummy-code")).thenReturn(profile);
        identityService.linkGithub(userId, "dummy-code");
        verify(identityMapRepositoryPort).deleteByInternalUserIdAndExternalProvider(userId, ExternalProvider.GITHUB);
        verify(identityMapRepositoryPort).save(any(IdentityMap.class));
    }

    @Test
    void testLinkJira_Success() {
        UUID userId = UUID.randomUUID();
        ExternalUserProfile profile = ExternalUserProfile.builder()
                .id("jira-account-id")
                .name("Jira User")
                .email("jira@example.com")
                .build();
        when(externalIdentityPort.getJiraProfile("dummy-code")).thenReturn(profile);
        identityService.linkJira(userId, "dummy-code");
        verify(identityMapRepositoryPort).deleteByInternalUserIdAndExternalProvider(userId, ExternalProvider.JIRA);
        verify(identityMapRepositoryPort).save(any(IdentityMap.class));
    }
}
