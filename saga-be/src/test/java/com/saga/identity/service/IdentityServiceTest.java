package com.saga.identity.service;

import com.saga.identity.repository.JpaIdentityMapRepository;
import com.saga.identity.entity.ExternalProvider;
import com.saga.identity.entity.IdentityMap;
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
    private JpaIdentityMapRepository identityMapRepository;
    @Mock
    private ExternalIdentityService externalIdentityPort;
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
        verify(identityMapRepository).deleteByInternalUserIdAndExternalProvider(userId, ExternalProvider.GITHUB);
        verify(identityMapRepository).save(any(IdentityMap.class));
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
        verify(identityMapRepository).deleteByInternalUserIdAndExternalProvider(userId, ExternalProvider.JIRA);
        verify(identityMapRepository).save(any(IdentityMap.class));
    }
}
